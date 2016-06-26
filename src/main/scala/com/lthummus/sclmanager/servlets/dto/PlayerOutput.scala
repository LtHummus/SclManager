package com.lthummus.sclmanager.servlets.dto

import zzz.generated.tables.records.PlayerRecord


case class Player(id: Int, leagueId: Int, name: String, wins: Int, draws: Int, losses: Int, matches: Option[List[Match]]) {
  val matchesPlayed = wins + draws + losses
}

case class PlayerList(players: List[Player])

object Player {
  def fromDatabaseRecord(record: PlayerRecord, matches: Option[List[Match]] = None) =
    Player(record.getId,
      record.getLeague,
      record.getName,
      record.getWins,
      record.getDraws,
      record.getLosses,
      matches)
}