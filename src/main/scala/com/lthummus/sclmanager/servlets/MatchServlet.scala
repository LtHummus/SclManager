package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.{MatchDao, PlayerDao}
import org.jooq.DSLContext
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{NotFound, Ok}
import org.scalatra.json.JacksonJsonSupport


class MatchServlet(implicit dslContext: DSLContext) extends SclManagerStack with JacksonJsonSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

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
