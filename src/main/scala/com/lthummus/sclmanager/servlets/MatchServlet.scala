package com.lthummus.sclmanager.servlets

import com.amazonaws.util.IOUtils
import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.TransactionSupport
import com.lthummus.sclmanager.database.dao.{BoutDao, DraftDao, GameDao, PlayerDao}
import com.lthummus.sclmanager.parsing.{Bout, BoutTypeEnum, SpyPartyZipParser}
import org.jooq.DSLContext
import org.json4s.{DefaultFormats, FieldSerializer, Formats}
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.servlet.{FileItem, FileUploadSupport, MultipartConfig}
import zzz.generated.tables.records.{BoutRecord, DraftRecord, GameRecord, PlayerRecord}
import com.lthummus.sclmanager.database.dao.GameDao._
import com.lthummus.sclmanager.scaffolding.SystemConfig
import com.lthummus.sclmanager.scaffolding.SystemConfig._
import com.lthummus.sclmanager.servlets.dto._
import com.lthummus.sclmanager.util.S3Uploader
import com.typesafe.config.ConfigFactory
import org.apache.commons.io.FilenameUtils
import org.json4s.ext.JodaTimeSerializers
import org.scalatra.swagger.{Swagger, SwaggerEngine, SwaggerSupport}

import scalaz._
import Scalaz._
import scala.util.Try

object MatchServlet {
  val Uploader = new S3Uploader()
}

class MatchServlet(implicit dslContext: DSLContext, val swagger: Swagger) extends SclManagerStack with JacksonJsonSupport
                                                                            with FileUploadSupport
                                                                            with SwaggerSupport
                                                                            with TransactionSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats ++ JodaTimeSerializers.all + FieldSerializer[Game]()

  protected val applicationDescription = "Gets match information"
  private val ForfeitPassword = ConfigFactory.load().getEncryptedString("forefitPassword")

  configureMultipartHandling(MultipartConfig(maxFileSize = Some(3 * 1024 * 1024))) // 3 megabytes

  before() {
    contentType = formats("json")
  }

  private def forfeitMatch(id: Int, winner: String, text: String) = {
    def winnerIsValid(winner: String, player1Name: String, player2Name: String) = {
      if (winner == player1Name || winner == player2Name)
        ().right
      else
        s"$winner is not valid for this match".left
    }

    def checkStatus(bout: BoutRecord) = {
      if (bout.getStatus == 0) {
        ().right
      } else {
        "Match has already been played or forfeited".left
      }
    }

    insideTransaction { implicit dslContext =>
      for {
        bout <- BoutDao.getById(id) \/> "There is no match with that id"
        _ <- winnerIsValid(winner, bout.getPlayer1, bout.getPlayer2)
        _ <- checkStatus(bout)
        _ <- BoutDao.updateBoutForfeitStatus(id, winner, text)
        _ <- PlayerDao.postResult(bout.getPlayer1, if (bout.getPlayer1 == winner) "win" else "loss")
        _ <- PlayerDao.postResult(bout.getPlayer2, if (bout.getPlayer2 == winner) "win" else "loss")
      } yield {
        bout
      }
   }
  }

  private def updateScores(bout: Bout, games: List[GameRecord], url: String, draft: Option[DraftRecord]): String \/ Int = {
    val player1Res = bout.result(bout.player1)
    val player2Res = bout.result(bout.player2)

    for {
      _ <- GameDao.persistBatchRecords(games)
      _ <- BoutDao.markBoutAsPlayed(games.head.getBout, url, draft)
      _ <- PlayerDao.postResult(bout.player1, player1Res)
      _ <- PlayerDao.postResult(bout.player2, player2Res)
    } yield games.head.getBout
  }

  private def persistBout(bout: Bout, url: String) = {
    insideTransaction{ implicit dslContext =>
      for {
        player1 <- PlayerDao.getByPlayerName(bout.player1) \/> s"No player found with name ${bout.player1}"
        player2 <- PlayerDao.getByPlayerName(bout.player2) \/> s"No player found with name ${bout.player2}"
        boutObj <- BoutDao.getNextToBePlayedByPlayers(player1.getName, player2.getName) \/> s"No match found between these players"
        draft = DraftDao.getLatestUnusedDraftForPlayer(Seq(player1.getName, player2.getName))
        updateRes <- updateScores(bout, bout.orderedReplays.map(_.toDatabase(boutObj.getId)), url, draft)
      } yield updateRes
    }
  }

  private def uploadToS3(name: String, contents: Array[Byte]) = {
    MatchServlet.Uploader.putReplay(name, contents)
  }

  private def generateFilename(originalName: String, bout: Bout, record: BoutRecord) = {
    f"SCL Season 3 - Week ${record.getWeek.toInt}%02d - ${record.getDivision} - ${bout.player1} vs ${bout.player2}.${FilenameUtils.getExtension(originalName)}"
  }

  val forfeit = (apiOperation[Map[String, String]]("forfeit")
    summary "Forfeit a match given the information provided"
    parameter pathParam[String]("id").description("id of the match")
    parameter bodyParam[MatchForfeitInput].description("information on the match to forfeit"))

  put("/:id/forfeit", operation(forfeit)) {
    val data = parsedBody.extract[MatchForfeitInput]
    val matchId = params("id").toInt
    if (ForfeitPassword != data.password) {
      Forbidden(ErrorMessage("incorrect password"))
    } else {
      forfeitMatch(matchId, data.winnerName, data.text) match {
        case -\/(error) => InternalServerError(ErrorMessage(error.toString))
        case \/-(_) => Ok("message" -> "ok")
      }
    }
  }

  post("/parse") {
    val file = fileParams("file")

    val zipContents = IOUtils.toByteArray(file.getInputStream)

    val result = for {
      replayList <- SpyPartyZipParser.parseZipStream(zipContents)
      bout <- BoutDao.getNextToBePlayedByPlayers(replayList.head.spy, replayList.head.sniper) \/> "No match found between these two players"
      parseResult <- Try(Bout(replayList, BoutTypeEnum.fromInt(bout.getBoutType))).toDisjunction.leftMap(_.getMessage)
      url <- uploadToS3(generateFilename(file.name, parseResult, bout), zipContents)
      result <- persistBout(parseResult, url)
    } yield result

    result match {
      case -\/(error) => BadRequest(ErrorMessage(error))
      case \/-(boutId) =>
        BoutDao.getFullBoutRecords(boutId) match {
          case -\/(error) => InternalServerError(ErrorMessage(error))
          case \/-(it)    => Ok(Match.fromDatabaseRecordWithGames(it.bout, it.games, it.playerMap, it.draft))
        }
    }
  }


  if (SystemConfig.isTest) {
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
  }



  private val getByWeek = (apiOperation[Match]("getById")
    summary "Get matches by their week"
    parameter pathParam[String]("week").description("the week number to get"))

  get("/week/:week", operation(getByWeek)) {
    val week = params("week").toInt
    val players = PlayerDao.all()
    Ok(BoutDao.getByWeek(week).map(m => Match.fromDatabaseRecordWithGames(m, GameDao.getGameRecordsByBoutId(m.getId), players.map(it => (it.getName, it)).toMap, None)))
  }

  get("/next/:player1/:player2") {
    val player1Param = params("player1")
    val player2Param = params("player2")

    val player1Res = PlayerDao.getByPlayerName(player1Param)
    val player2Res = PlayerDao.getByPlayerName(player2Param)

    val result = for {
      player1Obj <- player1Res
      player2Obj <- player2Res
      matchObj <- BoutDao.getNextToBePlayedByPlayers(player1Obj.getName, player2Obj.getName)
    } yield (matchObj, player1Obj, player2Obj)

    result match {
      case None => NotFound(ErrorMessage("No match found"))
      case Some(it) =>
        val m = it._1
        val p1 = it._2
        val p2 = it._3
        val playerMap = Map(p1.getName -> p1, p2.getName -> p2)
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
      case \/-(it) => Ok(Match.fromDatabaseRecordWithGames(it.bout, it.games, it.playerMap, it.draft))
    }
  }


  get("/last") {
    val bout = BoutDao.getLastUploaded()

    Ok(Map("player1" -> bout.getPlayer1, "player2" -> bout.getPlayer2))
  }

  private val getAll = (apiOperation[Match]("getAll")
    summary "get all the matches")

  get("/all", operation(getAll)) {
    val players = PlayerDao.all()
    Ok(BoutDao.getAll().map(Match.fromDatabaseRecordWithGames(_, List(), players.map(it => (it.getName, it)).toMap, None)))
  }

}
