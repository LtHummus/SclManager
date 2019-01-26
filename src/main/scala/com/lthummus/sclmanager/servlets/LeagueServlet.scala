package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.{DivisionDao, PlayerDao}
import com.lthummus.sclmanager.servlets.dto.{ErrorMessage, League, LeagueList, LeagueOverview}
import org.jooq.DSLContext
import org.json4s.ext.JodaTimeSerializers
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{NotFound, Ok}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.swagger._


class LeagueServlet(implicit dslContext: DSLContext, val swagger: Swagger) extends SclManagerStack with JacksonJsonSupport with SwaggerSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats ++ JodaTimeSerializers.all

  protected val applicationDescription = "Gets league information"

  before() {
    contentType = formats("json")
  }

  val getAll = (apiOperation[LeagueOverview]("getAll")
    summary "Get league overview"
    notes "Returns nice summary data and a current snapshot of the league"
    )

  get("/", operation(getAll)) {
    val leagueDatabaseRecords = DivisionDao.all()
    val playerDatabaseRecords = PlayerDao.all()

    val leagues = leagueDatabaseRecords.map(l => League.fromDatabaseRecord(l, playerDatabaseRecords.filter(_.divisionName == l.getName))).sorted

    Ok(LeagueOverview(LeagueList(leagues)))
  }

  val getByName = (apiOperation[League]("getByName")
    summary "Get league data by a league's name"
    notes "Name must match exactly"
    parameter pathParam[String]("name").description("the name of the league to look up"))

  get("/:name", operation(getByName)) {
    //TODO: error check the input
    val name = params("name")

    val league = DivisionDao.getByName(name)
    val players = PlayerDao.getPlayersInDivision(name)

    league match {
      case None => NotFound(ErrorMessage(s"No league with id $name found"))
      case Some(it) => Ok(League.fromDatabaseRecord(it, players))
    }
  }

  get("/:name/matches") {
    val name = params("name")

    val players = PlayerDao.getPlayersInDivision(name)


  }
}
