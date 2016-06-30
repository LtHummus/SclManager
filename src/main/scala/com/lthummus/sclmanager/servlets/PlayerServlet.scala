package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.{DivisionDao, BoutDao, PlayerDao}
import com.lthummus.sclmanager.servlets.dto.{Match, Player}
import org.jooq.DSLContext
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{NotFound, Ok}
import org.scalatra.json.JacksonJsonSupport

class PlayerServlet(implicit dslContext: DSLContext) extends SclManagerStack with JacksonJsonSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  get("/:name") {
    val name = params("name")

    PlayerDao.getByPlayerName(name) match {
      case None => NotFound(s"No player with name $name found")
      case Some(player) => Ok(Player.fromDatabaseRecord(player))
    }
  }

  get("/:name/matches") {
    val name = params("name")

    val playerRes = PlayerDao.getByPlayerName(name)

    val decoded = for {
      player <- playerRes
      leaguePlayers = DivisionDao.getPlayersInLeague(player.getDivision)
      playerMap = leaguePlayers.map(it => (it.getName, it)).toMap
      matchRecords = BoutDao.getMatchesForPlayer(player.getName)
      matches = matchRecords.map(Match.fromDatabaseRecordWithGames(_, None, playerMap))
    } yield Player.fromDatabaseRecord(player, Some(matches))

    decoded match {
      case None => NotFound(s"No player with id $name found")
      case Some(player) => Ok(player)
    }
  }
}
