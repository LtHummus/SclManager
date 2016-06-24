package com.lthummus.sclmanager.servlets

import com.amazonaws.util.IOUtils
import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.{GameDao, MatchDao, PlayerDao}
import com.lthummus.sclmanager.parsing.{Bout, SpyPartyZipParser}
import org.jooq.DSLContext
import org.json4s.{DefaultFormats, Formats}
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.servlet.{FileUploadSupport, MultipartConfig}
import zzz.generated.tables.records.{GameRecord, PlayerRecord}
import com.lthummus.sclmanager.database.dao.GameDao._
import com.lthummus.sclmanager.servlets.dto.Match
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

  private def generateIdResolver(player1: PlayerRecord, player2: PlayerRecord) = {
    new PartialFunction[String, Int] {
      val player1Name = player1.getName
      val player2Name = player2.getName

      override def isDefinedAt(x: String): Boolean = x == player1.getName || x == player2.getName

      override def apply(x: String): Int = {
        x match {
          case `player1Name` => player1.getId
          case `player2Name` => player2.getId
          case _ => ???
        }
      }
    }
  }

  private def updateScores(bout: Bout, games: List[GameRecord], url: String): String \/ Int = {
    val player1Res = bout.result(bout.player1)
    val player2Res = bout.result(bout.player2)

    for {
      player1Update <- PlayerDao.postResult(bout.player1, player1Res)
      player2Update <- PlayerDao.postResult(bout.player2, player2Res)
      boutPersist <- GameDao.persistBatchRecords(games)
      markMatch <- MatchDao.markMatchAsPlayed(games.head.getMatch, url)
    } yield markMatch
  }

  private def persistBout(bout: Bout, url: String) = {
    for {
      player1 <- PlayerDao.getPlayerByName(bout.player1).toRightDisjunction(s"No player found with name ${bout.player1}")
      player2 <- PlayerDao.getPlayerByName(bout.player2).toRightDisjunction(s"No player found with name ${bout.player2}")
      resolver = generateIdResolver(player1, player2)
      matchObj <- MatchDao.getNextToBePlayedByPlayers(player1.getId, player2.getId).toRightDisjunction(s"No match found between these players")
      updateRes <- updateScores(bout, bout.orderedReplays.map(_.toDatabase(matchObj.getId, resolver)), url)
    } yield updateRes
  }

  private def uploadToS3(name: String, contents: Array[Byte]) = {
    MatchServlet.Uploader.putReplay(name, contents)
  }

  // TODO: pull upload from body?
  post("/parse") {
    val file = fileParams("file")

    val zipContents = IOUtils.toByteArray(file.getInputStream)

    println("read contents")

    val result = for {
      parseResult <- SpyPartyZipParser.parseZipStream(zipContents)
      url <- uploadToS3(file.name, zipContents)
      result <- persistBout(parseResult, url)
    } yield result

    result match {
      case -\/(error) => BadRequest(error)
      case \/-(_) => Ok("match persisted ok")
    }

  }

  get("/next/:player1/:player2") {
    val player1Param = params("player1")
    val player2Param = params("player2")

    val player1Res = PlayerDao.getPlayerByName(player1Param)
    val player2Res = PlayerDao.getPlayerByName(player2Param)

    val result = for {
      player1Obj <- player1Res
      player2Obj <- player2Res
      matchObj <- MatchDao.getNextToBePlayedByPlayers(player1Obj.getId, player2Obj.getId)
    } yield (matchObj, player1Obj, player2Obj)

    result match {
      case None => NotFound("No match found")
      case Some(it) =>
        val m = it._1
        val p1 = it._2
        val p2 = it._3
        val playerMap = Map(p1.getId -> p1, p2.getId -> p2)
        Ok(Match.fromDatabaseRecordWithGames(m, None, playerMap))
    }
  }

  get("/:id") {
    val bout = MatchDao.getFullMatchRecords(params("id").toInt)

    bout match {
      case -\/(error) => BadRequest(error)
      case \/-(it) => Match.fromDatabaseRecordWithGames(it.record, it.games, it.playerMap)
    }
  }
}
