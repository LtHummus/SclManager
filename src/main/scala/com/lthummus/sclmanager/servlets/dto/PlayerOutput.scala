package com.lthummus.sclmanager.servlets.dto

import zzz.generated.tables.records.PlayerRecord


case class Player(divisionName: String, name: String, country: String, wins: Int, draws: Int, losses: Int, matches: Option[List[Match]]) {
  val matchesPlayed = wins + draws + losses
}

case class PlayerList(players: List[Player])

object Player {
  def fromDatabaseRecord(record: PlayerRecord, matches: Option[List[Match]] = None) =
    Player(record.getDivision,
      record.getName,
      record.getCountry,
      record.getWins,
      record.getDraws,
      record.getLosses,
      matches)
}