package com.lthummus.sclmanager.servlets.dto

import com.lthummus.sclmanager.parsing.{Bout, GameResult}
import zzz.generated.tables.records.{GameRecord, BoutRecord, PlayerRecord}

import scalaz._
import Scalaz._

case class Match(id: Int, week: Int, league: String, player1: Player, player2: Player, status: Int, winner: Option[Player], games: Option[List[Game]], matchUrl: Option[String])

case class Game(id: Int, spy: Player, sniper: Player, matchId: Int, result: String, level: String, gameType: String)

object Match {

  def fromDatabaseRecordWithGames(record: BoutRecord, games: Option[List[GameRecord]], playerMap: Map[String, PlayerRecord]) = {
    val gameList = games.map(x => x.map(Game.fromDatabaseRecord(_, playerMap)))
    val winner = if (record.getWinner == null) None else Some(Player.fromDatabaseRecord(playerMap(record.getWinner)))
    val packagedMatchUrl = Option(record.getMatchUrl)
    Match(record.getId,
      record.getWeek,
      record.getDivision,
      Player.fromDatabaseRecord(playerMap(record.getPlayer1)),
      Player.fromDatabaseRecord(playerMap(record.getPlayer2)),
      record.getStatus,
      winner,
      gameList,
      packagedMatchUrl)
  }
}

object Game {
  def fromDatabaseRecord(record: GameRecord, playerMap: Map[String, PlayerRecord]) = {
    val gameResult = GameResult.fromInt(record.getResult) match {
      case -\/(error) => error
      case \/-(res) => res.toString
    }
    Game(record.getId,
      Player.fromDatabaseRecord(playerMap(record.getSpy)),
      Player.fromDatabaseRecord(playerMap(record.getSniper)),
      record.getBout,
      gameResult,
      record.getVenue,
      record.getGametype)
  }
}