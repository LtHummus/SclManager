package com.lthummus.sclmanager.servlets

import com.amazonaws.util.IOUtils
import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.TransactionSupport
import com.lthummus.sclmanager.database.dao.GameDao._
import com.lthummus.sclmanager.database.dao.{BoutDao, DraftDao, GameDao, PlayerDao}
import com.lthummus.sclmanager.parsing._
import com.lthummus.sclmanager.scaffolding.SclManagerConfig
import com.lthummus.sclmanager.servlets.dto._
import com.lthummus.sclmanager.util.{DiscordPoster, MatchForfeits, S3Uploader, SpypartyFansWebhook}
import org.apache.commons.io.FilenameUtils
import org.jooq.DSLContext
import org.json4s.ext.JodaTimeSerializers
import org.json4s.{DefaultFormats, FieldSerializer, Formats}
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.servlet.{FileUploadSupport, MultipartConfig}
import org.scalatra.swagger.{Swagger, SwaggerSupport}
import org.slf4j.LoggerFactory
import scalaz.Scalaz._
import scalaz._
import zzz.generated.Tables
import zzz.generated.tables.records.{BoutRecord, DraftRecord, GameRecord}

import scala.util.Try

object MatchServlet {
  private val Uploader = new S3Uploader()
  private val Logger = LoggerFactory.getLogger("MatchServlet")
}

class MatchServlet(implicit dslContext: DSLContext, val swagger: Swagger) extends SclManagerStack with JacksonJsonSupport
                                                                            with FileUploadSupport
                                                                            with SwaggerSupport
                                                                            with TransactionSupport {
  import MatchServlet._

  protected implicit lazy val jsonFormats: Formats = DefaultFormats ++ JodaTimeSerializers.all + FieldSerializer[Game]()

  protected val applicationDescription = "Gets match information"
  private val ForfeitPassword = SclManagerConfig.forfeitPassword

  configureMultipartHandling(MultipartConfig(maxFileSize = Some(3 * 1024 * 1024))) // 3 megabytes

  before() {
    contentType = formats("json")
  }

  private def updateScores(bout: Bout, games: List[GameRecord], url: String, draft: Option[DraftRecord]): String \/ Int = {
    val player1Res = bout.result(bout.player1)
    val player2Res = bout.result(bout.player2)

    draft match {
      case None    => Logger.warn("Could not find appropriate draft for bout {}", games.head.getBout)
      case Some(d) => Logger.info("Matched draft id {} with room code {} to bout {}", d.getId, d.getRoomCode, games.head.getBout)
    }

    for {
      _ <- GameDao.persistBatchRecords(games)
      _ <- BoutDao.markBoutAsPlayed(games.head.getBout, url, draft)
      _ <- PlayerDao.postResult(bout.player1, player1Res)
      _ <- PlayerDao.postResult(bout.player2, player2Res)
    } yield games.head.getBout
  }

  private def persistBout(bout: Bout, url: String) = {
    Logger.info("Persisting bout between {} ({}) and {} ({})", bout.player1, bout.player1Score: Integer, bout.player2, bout.player2Score: Integer)

    insideTransaction{ implicit dslContext =>
      for {
        player1   <- PlayerDao.getByPlayerName(bout.player1) \/> s"No player found with name ${bout.player1}"
        player2   <- PlayerDao.getByPlayerName(bout.player2) \/> s"No player found with name ${bout.player2}"
        boutObj   <- BoutDao.getNextToBePlayedByPlayers(player1.name, player2.name) \/> s"No match found between these players"
        draft     =  DraftDao.getLatestUnusedDraftForPlayer(Seq(player1.name, player2.name))
        updateRes <- updateScores(bout, bout.orderedReplays.map(_.toDatabase(boutObj.getId)), url, draft)
      } yield updateRes
    }
  }

  private def generateFilename(originalName: String, bout: Bout, record: BoutRecord) = {
    f"SCL Season ${SclManagerConfig.sclSeasonNumber} - Week ${record.getWeek.toInt}%02d - ${record.getDivision} - ${bout.player1} vs ${bout.player2}.${FilenameUtils.getExtension(originalName)}"
  }

  private def patchIfNecessary(zipBytes: Array[Byte], nameChanges: Map[String, String], oldReplays: List[Replay]): String \/ List[Replay] = {
    if (nameChanges.forall{ case (a, b) => a == b}) {
      oldReplays.right
    } else {
      Logger.info("Patching replays: {}", nameChanges.toString)

      for {
        patched <- ZipFilePatcher.patchZipFile(zipBytes, nameChanges)
        replays <- SpyPartyZipParser.parseZipStream(patched)
      } yield replays
    }
  }

  post("/:id/forceSpf") {
    request.header("Authentication") match {
      case Some(x) if x == SclManagerConfig.forfeitPassword => //nop
      case _                                                => Logger.warn("Invalid password. Stopping."); halt(Forbidden("No"))
    }

    val matchId = params("id").toInt

    val fullData = for {
      boutRecord <- BoutDao.getFullBoutRecords(matchId)
    } yield {
      Match.fromDatabaseRecordWithGames(boutRecord.bout, boutRecord.games, boutRecord.playerMap, boutRecord.draft)
    }

    fullData match {
      case -\/(error) => BadRequest(s"Couldn't get that match. Error: $error")
      case \/-(boutData) =>
        SpypartyFansWebhook.postToWebhook(boutData)
        Ok("result" -> "ok")
    }
  }

  val forfeit = (apiOperation[Map[String, String]]("forfeit")
    summary "Forfeit a match given the information provided"
    parameter bodyParam[MatchForfeitInput].description("information on the match to forfeit"))

  put("/forfeit", operation(forfeit)) {
    val data = parsedBody.extract[MatchForfeitInput]

    if (data.password != ForfeitPassword) {
      Logger.warn("Attempted to forfeit with incorrect password")
      Forbidden("error" -> "Invalid password")
    } else {
      if (data.kind == "single") {
        MatchForfeits.forfeitMatch(data.id, data.winner, "Admin Forfiet") match {
          case -\/(error) => InternalServerError("error" -> error)
          case \/-(_)     => Ok("message" -> "ok")
        }
      } else if (data.kind == "double") {
        MatchForfeits.doubleForfeit(data.id, "Admin Forfeit") match {
          case -\/(error) => InternalServerError("error" -> error)
          case \/-(_)     => Ok("message" -> "ok")
        }
      } else {
        BadRequest("error" -> "Invalid forfeit type")
      }
    }

  }

  post("/parse") {
    val file = fileParams("file")

    val zipContents = IOUtils.toByteArray(file.getInputStream)

    //XXX: this whole business with name changes is because of the way that spyparty handles steam names in replays
    //     it's super inefficient, but that's ok
    val result = for {
      oldReplayList   <- SpyPartyZipParser.parseZipStream(zipContents)
      player1RealName <- PlayerDao.getPlayerFromReplayName(oldReplayList.head.spy) \/> s"Could not find player with name ${oldReplayList.head.spy}"
      player2RealName <- PlayerDao.getPlayerFromReplayName(oldReplayList.head.sniper) \/> s"Could not find player with name ${oldReplayList.head.sniper}"
      nameChanges     =  Map(oldReplayList.head.spy -> player1RealName, oldReplayList.head.sniper -> player2RealName)
      replayList      <- patchIfNecessary(zipContents, nameChanges, oldReplayList)
      bout            <- BoutDao.getNextToBePlayedByPlayers(player1RealName, player2RealName) \/> s"No match found between $player1RealName and $player2RealName"
      parseResult     <- Try(Bout(replayList, BoutTypeEnum.fromInt(bout.getBoutType))).toDisjunction.leftMap(_.getMessage)
      url             <- MatchServlet.Uploader.putReplay(generateFilename(file.name, parseResult, bout), zipContents)
      result          <- persistBout(parseResult, url)
    } yield result

    result match {
      case -\/(error) =>
        Logger.warn("Error persisting bout. Got error {}", error)
        BadRequest(ErrorMessage(error))

      case \/-(boutId) =>
        BoutDao.getFullBoutRecords(boutId) match {
          case -\/(error) => Logger.warn("Error getting bout information post persist: {}", error); InternalServerError(ErrorMessage(error))
          case \/-(it)    =>
            val fullData = Match.fromDatabaseRecordWithGames(it.bout, it.games, it.playerMap, it.draft)
            DiscordPoster.post(fullData)
            if (SclManagerConfig.enableSpypartyFans) SpypartyFansWebhook.postToWebhook(fullData)
            Logger.info("Successfully persisted bout {}", fullData.id)
            Ok(fullData)
        }
    }
  }


  if (SclManagerConfig.debugMode) {
    val resetById = (apiOperation[Match]("resetById")
      summary "Reset matches to their default state"
      notes "THIS IS FOR TESTING ONLY -- DELETES ALL DATA ASSOCIATED WITH A MATCH"
      parameter pathParam[String]("id").description("id to reset"))

    put("/reset/:id", operation(resetById)) {
      val id = params("id").toInt

      GameDao.deleteBelongingToMatch(id)
      val finalResult = BoutDao.resetBout(id)

      finalResult match {
        case -\/(_) => InternalServerError("result" -> "something screwed up")
        case \/-(_) => Ok("result" -> "ok")
      }
    }

    get("/bulk") {
      val players = PlayerDao.all()
      val everything = BoutDao.getAll().map(Match.fromDatabaseRecordWithGames(_, List(), players.map(it => (it.name, it)).toMap, None))
      val ids = everything.map(_.id)

      Ok(ids.map(curr => {
        val res = BoutDao.getFullBoutRecords(curr)
        val thing = res.getOrElse(throw new IllegalArgumentException())
        thing
      }))
    }
  }

  private val getByWeek = (apiOperation[Match]("getById")
    summary "Get matches by their week"
    parameter pathParam[String]("week").description("the week number to get"))

  get("/week/:week", operation(getByWeek)) {
    val week = Math.min(params("week").toInt, SclManagerConfig.seasonLength)
    val players = PlayerDao.all()
    Ok(BoutDao.getByWeek(week).map(m => {
      Match.fromDatabaseRecordWithGames(m, GameDao.getGameRecordsByBoutId(m.into(Tables.BOUT).getId), players.map(it => (it.name, it)).toMap, None)
    }))
  }

  get("/next/:player1/:player2") {
    val player1Param = params("player1")
    val player2Param = params("player2")

    val player1Res = PlayerDao.getByPlayerName(player1Param)
    val player2Res = PlayerDao.getByPlayerName(player2Param)

    val result = for {
      player1Obj <- player1Res
      player2Obj <- player2Res
      matchObj <- BoutDao.getNextToBePlayedByPlayers(player1Obj.name, player2Obj.name)
    } yield (matchObj, player1Obj, player2Obj)

    result match {
      case None => NotFound(ErrorMessage("No match found"))
      case Some(it) =>
        val m = it._1
        val p1 = it._2
        val p2 = it._3
        val playerMap = Map(p1.name -> p1, p2.name -> p2)
        Ok(Match.fromDatabaseRecordWithGames(m, List(), playerMap, None))
    }
  }

  private val getById = (apiOperation[Match]("getById")
    summary "Get a match by its id"
    parameter pathParam[String]("id").description("the id of the match to get"))

  get("/:id", operation(getById)) {
    val bout = BoutDao.getFullBoutRecords(params("id").toInt)

    bout match {
      case -\/(error) => BadRequest(ErrorMessage(error))
      case \/-(it)    => Ok(Match.fromDatabaseRecordWithGames(it.bout, it.games, it.playerMap, it.draft))
    }
  }

  get("/:id/discord") {
    val bout = BoutDao.getFullBoutRecords(params("id").toInt)

    bout match {
      case -\/(error) => BadRequest(ErrorMessage(error))
      case \/-(it)    =>
        val fullData = Match.fromDatabaseRecordWithGames(it.bout, it.games, it.playerMap, it.draft)
        Ok(fullData.discordPost)
    }
  }


  get("/last") {
    val bout = BoutDao.getLastUploaded()

    Ok(Map("player1" -> bout.getPlayer1, "player2" -> bout.getPlayer2, "id" -> bout.getId, "matchUrl" -> bout.getMatchUrl))
  }

  private val getAll = (apiOperation[Match]("getAll")
    summary "get all the matches")

  get("/all", operation(getAll)) {
    val players = PlayerDao.all()
    Ok(BoutDao.getAll().map(Match.fromDatabaseRecordWithGames(_, List(), players.map(it => (it.name, it)).toMap, None)))
  }


}
