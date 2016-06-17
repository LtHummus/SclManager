package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.{GameDao, MatchDao, PlayerDao}
import com.lthummus.sclmanager.database.data.{Game, Player}
import com.lthummus.sclmanager.parsing.{Bout, SpyPartyZipParser}
import org.jooq.DSLContext
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{BadRequest, NotFound, Ok}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.servlet.{FileUploadSupport, MultipartConfig}

import scalaz._
import Scalaz._

class MatchServlet(implicit dslContext: DSLContext) extends SclManagerStack with JacksonJsonSupport
                                                                            with FileUploadSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  configureMultipartHandling(MultipartConfig(maxFileSize = Some(3 * 1024 * 1024))) // 3 megabytes

  before() {
    contentType = formats("json")
  }

  private def generateIdResolver(player1: Player, player2: Player) = {
    new PartialFunction[String, Int] {
      override def isDefinedAt(x: String): Boolean = x == player1.name || x == player2.name

      override def apply(x: String): Int = {
        x match {
          case player1.name => player1.id
          case player2.name => player2.id
          case _ => ???
        }
      }
    }
  }

  private def persistBout(bout: Bout) = {
    val records = for {
      player1 <- PlayerDao.getPlayerByName(bout.player1).toRightDisjunction(s"No player found with name ${bout.player1}")
      player2 <- PlayerDao.getPlayerByName(bout.player2).toRightDisjunction(s"No player found with name ${bout.player2}")
      resolver = generateIdResolver(player1, player2)
      matchObj <- MatchDao.getNextToBePlayedByPlayers(player1.id, player2.id).toRightDisjunction(s"No match found between these players")
    } yield bout.orderedReplays.map(Game.toDb(_, matchObj.id, resolver))

    records match {
      case -\/(error) => BadRequest(error)
      case \/-(it) => Ok(GameDao.persistBatchRecords(it))
    }
  }

  // TODO: pull upload from body?
  post("/parse") {
    val file = fileParams("file")

    val parseResult = SpyPartyZipParser.parseZipStream(file.getInputStream)

    parseResult match {
      case -\/(error) => BadRequest("Could not parse zip file: " + error)
      case \/-(results) => persistBout(results)
    }
  }

  get("/next/:player1/:player2") {
    val player1 = params("player1")
    val player2 = params("player2")

    val result = for {
      player1Obj <- PlayerDao.getPlayerByName(player1)
      player2Obj <- PlayerDao.getPlayerByName(player2)
      matchObj <- MatchDao.getNextToBePlayedByPlayers(player1Obj.id, player2Obj.id)
    } yield matchObj


    result match {
      case None => NotFound("No match found")
      case Some(it) => Ok(it)
    }
  }

  get("/:id") {
    val bout = MatchDao.getBoutData(params("id").toInt)

    bout match {
      case -\/(error) => BadRequest(error)
      case \/-(it) => Ok(Map("score_line" -> it.getScoreLine, "summary" -> it.getGameSummary))
    }
  }
}
