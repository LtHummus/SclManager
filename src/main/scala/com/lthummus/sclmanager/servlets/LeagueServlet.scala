package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.{DivisionDao, PlayerDao}
import com.lthummus.sclmanager.servlets.dto.{League, LeagueList, LeagueOverview}
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
    val leagueDatabaseRecords = DivisionDao.all()
    val playerDatabaseRecords = PlayerDao.all()

    val leagues = leagueDatabaseRecords.map(l => League.fromDatabaseRecord(l, playerDatabaseRecords.filter(_.getDivision == l.getName)))

    Ok(LeagueOverview(LeagueList(leagues)))
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
