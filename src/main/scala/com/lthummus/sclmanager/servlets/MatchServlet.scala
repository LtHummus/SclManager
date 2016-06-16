package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.{MatchDao, PlayerDao}
import com.lthummus.sclmanager.parsing.SpyPartyZipParser
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

  // TODO: pull upload from body?
  post("/parse") {
    val file = fileParams("file")

    val parseResult = SpyPartyZipParser.parseZipStream(file.getInputStream)

    parseResult match {
      case -\/(error) => BadRequest("Could not parse zip file: " + error)
      case \/-(results) =>
        Map("score_line" -> results.getScoreLine, "summary" -> results.getGameSummary)
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
}
