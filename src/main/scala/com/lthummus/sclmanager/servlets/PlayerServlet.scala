package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.{BoutDao, DivisionDao, GameDao, PlayerDao}
import com.lthummus.sclmanager.scaffolding.SclManagerConfig
import com.lthummus.sclmanager.servlets.dto.{ErrorMessage, Match, Player}
import com.lthummus.sclmanager.util.MatchForfeits
import org.jooq.DSLContext
import org.json4s.ext.JodaTimeSerializers
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{BadRequest, Forbidden, InternalServerError, NotFound, Ok}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.swagger.{Swagger, SwaggerSupport}
import org.slf4j.LoggerFactory

object PlayerServlet {
  private val Logger = LoggerFactory.getLogger("PlayerServlet")
}

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

    val res = for {
      playerData <- PlayerDao.getByPlayerName(name)
      division   <- DivisionDao.getByName(playerData.divisionName)
    } yield {
      if (division.getSecret) {
        playerData.sanitize
      } else {
        playerData
      }
    }

    res match {
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
      division <- DivisionDao.getByName(player.divisionName)
      leaguePlayers = PlayerDao.getPlayersInDivision(player.divisionName)
      playerMap = leaguePlayers.map(it => (it.name, it)).toMap
      matchRecords = BoutDao.getMatchesForPlayer(player.name)
      matches = matchRecords.map(m => Match.fromDatabaseRecordWithGames(m, GameDao.getGameRecordsByBoutId(m.getId), playerMap, None))
    } yield {
      if (division.getSecret) {
        player.sanitize
      } else {
        player.withMatches(Some(matches))
      }
    }

    decoded match {
      case None => NotFound(ErrorMessage(s"No player with id $name found"))
      case Some(player) => Ok(player)
    }
  }

  post("/:name/revoke") {

    val name = params("name")

    PlayerServlet.Logger.info("Beginning forfeit process for {}", name)

    request.header("Authentication") match {
      case Some(x) if x == SclManagerConfig.forfeitPassword => //nop
      case _                                                => PlayerServlet.Logger.warn("Invalid password. Stopping."); halt(Forbidden("No"))
    }

    val matches = BoutDao.getMatchesForPlayer(name)

    if (matches.isEmpty) {
      PlayerServlet.Logger.warn("{} not found", name)
      halt(NotFound(s"player $name not found"))
    }

    if (matches.exists(_.getStatus == 1)) {
      //there are some played matches, deal with them manually
      PlayerServlet.Logger.warn("Found some matches that were played. Stopping")
      halt(BadRequest(s"Player $name has some played matches. You must unset them first"))
    }

    val forfeitWorklist = matches.map{ curr =>
      //figure out who the winner is....
      val winner = if (curr.getPlayer1 == name) {
        curr.getPlayer2
      } else if (curr.getPlayer2 == name) {
        curr.getPlayer1
      } else {
        PlayerServlet.Logger.warn("We found a match belonging to a player that they are not participating in. This should never happen")
        halt(InternalServerError("We found a match belonging to a player that they are not participating in. This should never happen"))
      }

      (curr.getId, winner, curr.getStatus)
    }

    PlayerServlet.Logger.info("Built forfeit worklist of {} matches", forfeitWorklist.size)

    val results = forfeitWorklist.map{ case (id, winner, status) =>
      if (status == 2) {
        //already forfeited, so don't touch it
        s"Skipping match $id because already forfeited"
      } else {
        PlayerServlet.Logger.info("Forfeiting match {}", id)
        val currResult = MatchForfeits.forfeitMatch(id, winner, s"$name Dropout")
        if (currResult.isRight) {
          s"Success forfeiting match $id"
        } else {
          s"Failed forfeiting match $id"
        }
      }
    }

    Ok("results" -> results)
  }
}
