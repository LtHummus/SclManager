package com.lthummus.sclmanager.servlets.dto

import zzz.generated.tables.records.PlayerRecord


case class Player(divisionName: String, name: String, country: String, wins: Int, draws: Int, losses: Int, matchesPlayed: Int, score: Int, matches: Option[List[Match]])



object Player {
  implicit class RichPlayer(player: PlayerRecord) {
    def getScore = 2 * player.getWins + player.getDraws
    def matchesPlayed = player.getWins + player.getDraws + player.getLosses
  }

  def fromDatabaseRecord(record: PlayerRecord, matches: Option[List[Match]] = None) =
    Player(record.getDivision,
      record.getName,
      record.getCountry,
      record.getWins,
      record.getDraws,
      record.getLosses,
      record.matchesPlayed,
      record.getScore,
      matches)
}