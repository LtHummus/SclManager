package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.{BoutDao, DivisionDao, GameDao, PlayerDao}
import com.lthummus.sclmanager.servlets.dto.{ErrorMessage, Match, Player}
import org.jooq.DSLContext
import org.json4s.ext.JodaTimeSerializers
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{NotFound, Ok}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.swagger.{Swagger, SwaggerSupport}

class PlayerServlet(implicit dslContext: DSLContext, val swagger: Swagger) extends SclManagerStack with JacksonJsonSupport with SwaggerSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats ++ JodaTimeSerializers.all

  protected val applicationDescription = "Get information on players"

  before() {
    contentType = formats("json")
  }

  val getByName = (apiOperation[Player]("getByName")
    summary "Get information about a player based on their name"
    notes "This does not include detailed match information"
    parameter pathParam[String]("name").description("name of the player to look up"))

  get("/:name", operation(getByName)) {
    val name = params("name")

    PlayerDao.getByPlayerName(name) match {
      case None => NotFound(ErrorMessage(s"No player with name $name found"))
      case Some(player) => Ok(player)
    }
  }


  val getDetailedInformation = (apiOperation[Player]("getByName")
    summary "Get detailed information on a player based on their name"
    notes "This is a more expensive query since it gets match data"
    parameter pathParam[String]("name").description("name of the player to look up"))

  get("/:name/matches", operation(getDetailedInformation)) {
    val name = params("name")

    val playerRes = PlayerDao.getByPlayerName(name)

    val decoded = for {
      player <- playerRes
      leaguePlayers = PlayerDao.getPlayersInDivision(player.divisionName)
      playerMap = leaguePlayers.map(it => (it.name, it)).toMap
      matchRecords = BoutDao.getMatchesForPlayer(player.name)
      matches = matchRecords.map(m => Match.fromDatabaseRecordWithGames(m, GameDao.getGameRecordsByBoutId(m.getId), playerMap, None))
    } yield player.withMatches(Some(matches))

    decoded match {
      case None => NotFound(ErrorMessage(s"No player with id $name found"))
      case Some(player) => Ok(player)
    }
  }
}
