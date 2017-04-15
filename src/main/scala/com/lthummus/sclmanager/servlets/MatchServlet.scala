package com.lthummus.sclmanager.servlets

import com.amazonaws.util.IOUtils
import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.{BoutDao, DraftDao, GameDao, PlayerDao}
import com.lthummus.sclmanager.parsing.{Bout, SpyPartyZipParser}
import org.jooq.DSLContext
import org.json4s.{DefaultFormats, Formats}
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.servlet.{FileUploadSupport, MultipartConfig}
import zzz.generated.tables.records.{BoutRecord, DraftRecord, GameRecord, PlayerRecord}
import com.lthummus.sclmanager.database.dao.GameDao._
import com.lthummus.sclmanager.servlets.dto.{BoutParseResults, ErrorMessage, Match}
import com.lthummus.sclmanager.util.S3Uploader
import org.apache.commons.io.FilenameUtils
import org.scalatra.swagger.{Swagger, SwaggerEngine, SwaggerSupport}

import scalaz._
import Scalaz._

object MatchServlet {
  val Uploader = new S3Uploader()
}

class MatchServlet(implicit dslContext: DSLContext, val swagger: Swagger) extends SclManagerStack with JacksonJsonSupport
                                                                            with FileUploadSupport
                                                                            with SwaggerSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  protected val applicationDescription = "Gets match information"

  configureMultipartHandling(MultipartConfig(maxFileSize = Some(3 * 1024 * 1024))) // 3 megabytes

  before() {
    contentType = formats("json")
  }

  private def updateScores(bout: Bout, games: List[GameRecord], url: String, draft: Option[DraftRecord]): String \/ Int = {
    val player1Res = bout.result(bout.player1)
    val player2Res = bout.result(bout.player2)

    for {
      _ <- PlayerDao.postResult(bout.player1, player1Res)
      _ <- PlayerDao.postResult(bout.player2, player2Res)
      _ <- GameDao.persistBatchRecords(games)
      _ <- BoutDao.markBoutAsPlayed(games.head.getBout, url, draft)
    } yield games.head.getBout
  }

  private def persistBout(bout: Bout, url: String) = {
    for {
      player1 <- PlayerDao.getByPlayerName(bout.player1) \/> s"No player found with name ${bout.player1}"
      player2 <- PlayerDao.getByPlayerName(bout.player2) \/> s"No player found with name ${bout.player2}"
      boutObj <- BoutDao.getNextToBePlayedByPlayers(player1.getName, player2.getName) \/> s"No match found between these players"
      draft = DraftDao.getLatestUnusedDraftForPlayer(Seq(player1.getName, player2.getName))
      updateRes <- updateScores(bout, bout.orderedReplays.map(_.toDatabase(boutObj.getId)), url, draft)
    } yield updateRes
  }

  private def uploadToS3(name: String, contents: Array[Byte]) = {
    MatchServlet.Uploader.putReplay(name, contents)
  }

  private def generateFilename(originalName: String, bout: Bout, record: BoutRecord) = {
    s"SCL Season 3 - Week ${record.getWeek} - ${bout.player1} vs ${bout.player2}.${FilenameUtils.getExtension(originalName)}"
  }

  post("/parse") {
    val file = fileParams("file")

    val zipContents = IOUtils.toByteArray(file.getInputStream)

    val result = for {
      parseResult <- SpyPartyZipParser.parseZipStream(zipContents)
      bout <- BoutDao.getNextToBePlayedByPlayers(parseResult.player1, parseResult.player2) \/> "No match found between these two players"
      url <- uploadToS3(generateFilename(file.name, parseResult, bout), zipContents)
      result <- persistBout(parseResult, url)
    } yield result

    result match {
      case -\/(error) => BadRequest(ErrorMessage(error))
      case \/-(bout) => redirect(s"/match/$bout")
    }
  }

  //TODO: see below
  /*
  FOR THE LOVE OF ALL THAT IS HOLY REMOVE THIS BEFORE FINAL RELEASE
   */
  put("/delete/:id") {
    val id = params("id").toInt

    GameDao.deleteBelongingToMatch(id)
    val finalResult = BoutDao.resetBout(id)

    finalResult match {
      case -\/(_) => InternalServerError("result" -> "something screwed up")
      case \/-(_) => Ok("result" -> "ok")
    }
  }

  get("/week/:week") {
    val week = params("week").toInt
    val players = PlayerDao.all()
    Ok(BoutDao.getByWeek(week).map(Match.fromDatabaseRecordWithGames(_, List(), players.map(it => (it.getName, it)).toMap, None)))
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

  get("/:id") {
    val bout = BoutDao.getFullBoutRecords(params("id").toInt)

    bout match {
      case -\/(error) => BadRequest(ErrorMessage(error))
      case \/-(it) => Ok(Match.fromDatabaseRecordWithGames(it.bout, it.games, it.playerMap, it.draft))
    }
  }

  get("/all") {
    val players = PlayerDao.all()
    Ok(BoutDao.getAll().map(Match.fromDatabaseRecordWithGames(_, List(), players.map(it => (it.getName, it)).toMap, None)))
  }

}
