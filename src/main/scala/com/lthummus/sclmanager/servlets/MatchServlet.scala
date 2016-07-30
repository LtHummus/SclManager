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
import zzz.generated.tables.records.{DraftRecord, GameRecord, PlayerRecord}
import com.lthummus.sclmanager.database.dao.GameDao._
import com.lthummus.sclmanager.servlets.dto.{ErrorMessage, Match}
import com.lthummus.sclmanager.util.S3Uploader

import scalaz._
import Scalaz._

object MatchServlet {
  val Uploader = new S3Uploader()
}

class MatchServlet(implicit dslContext: DSLContext) extends SclManagerStack with JacksonJsonSupport
                                                                            with FileUploadSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  configureMultipartHandling(MultipartConfig(maxFileSize = Some(3 * 1024 * 1024))) // 3 megabytes

  before() {
    contentType = formats("json")
  }

  private def updateScores(bout: Bout, games: List[GameRecord], url: String, draft: Option[DraftRecord]): String \/ Int = {
    val player1Res = bout.result(bout.player1)
    val player2Res = bout.result(bout.player2)

    for {
      player1Update <- PlayerDao.postResult(bout.player1, player1Res)
      player2Update <- PlayerDao.postResult(bout.player2, player2Res)
      boutPersist <- GameDao.persistBatchRecords(games)
      markMatch <- BoutDao.markBoutAsPlayed(games.head.getBout, url, draft)
    } yield markMatch
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
    "We did ok".right
    //MatchServlet.Uploader.putReplay(name, contents)
  }

  // TODO: pull upload from body?
  post("/parse") {
    val file = fileParams("file")

    val zipContents = IOUtils.toByteArray(file.getInputStream)

    val result = for {
      parseResult <- SpyPartyZipParser.parseZipStream(zipContents)
      url <- uploadToS3(file.name, zipContents)
      result <- persistBout(parseResult, url)
    } yield result

    result match {
      case -\/(error) => BadRequest(ErrorMessage(error))
      case \/-(_) => Ok("match persisted ok")
    }

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
        Ok(Match.fromDatabaseRecordWithGames(m, None, playerMap, None))
    }
  }

  get("/:id") {
    val bout = BoutDao.getFullBoutRecords(params("id").toInt)

    bout match {
      case -\/(error) => BadRequest(ErrorMessage(error))
      case \/-(it) => Ok(Match.fromDatabaseRecordWithGames(it.bout, Some(it.games), it.playerMap, it.draft))
    }
  }

  get("/all") {
    val players = PlayerDao.all()
    Ok(BoutDao.getAll().map(Match.fromDatabaseRecordWithGames(_, None, players.map(it => (it.getName, it)).toMap, None)))
  }
}
