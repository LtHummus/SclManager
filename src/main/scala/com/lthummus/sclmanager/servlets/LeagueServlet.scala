package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.PlayerDao
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
    Ok(LeagueList(LeagueDao.all().map(League.fromDatabaseRecord)))
  }

  get("/:id") {
    //TODO: error check the input
    val league = LeagueDao.getById(params("id").toInt)
    val players = PlayerDao.getByLeagueId(params("id").toInt)

    league match {
      case None => NotFound(s"No league with id ${params("id")} found")
      case Some(it) => Ok(League.fromDatabaseRecord(it, players))
    }
  }
}
