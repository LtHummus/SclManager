package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.{MatchDao, PlayerDao}
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

  get("/:id") {
    val playerId = params("id").toInt

    PlayerDao.getByPlayerId(playerId) match {
      case None => NotFound(s"No player with id $playerId found")
      case Some(player) => Ok(Player.fromDatabaseRecord(player))
    }
  }

  get("/:id/matches") {
    val playerId = params("id").toInt

    val playerRes = PlayerDao.getByPlayerId(playerId)

    val decoded = for {
      player <- playerRes
      leaguePlayers = LeagueDao.getPlayersInLeague(player.getId)
      playerMap = leaguePlayers.map(it => (it.getId, it)).toMap
      matchRecords = MatchDao.getMatchesForPlayer(playerId)
      matches = matchRecords.map(Match.fromDatabaseRecordWithGames(_, None, playerMap))
    } yield Player.fromDatabaseRecord(player, Some(matches))

    decoded match {
      case None => NotFound(s"No player with id $playerId found")
      case Some(player) => Ok(player)
    }
  }
}
