package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.{DivisionDao, PlayerDao}
import com.lthummus.sclmanager.servlets.dto.{League, LeagueList}
import org.jooq.DSLContext
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{NotFound, Ok}
import org.scalatra.json.JacksonJsonSupport


class LeagueServlet(implicit dslContext: DSLContext) extends SclManagerStack with JacksonJsonSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  get("/") {
    Ok(LeagueList(DivisionDao.all().map(League.fromDatabaseRecord)))
  }

  get("/:name") {
    //TODO: error check the input
    val league = DivisionDao.getByName(params("name"))
    val players = DivisionDao.getPlayersInLeague(params("name"))

    league match {
      case None => NotFound(s"No league with id ${params("id")} found")
      case Some(it) => Ok(League.fromDatabaseRecord(it, players))
    }
  }
}
