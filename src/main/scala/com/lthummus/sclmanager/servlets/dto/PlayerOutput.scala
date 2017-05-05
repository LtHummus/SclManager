package com.lthummus.sclmanager.servlets.dto

import zzz.generated.tables.records.PlayerRecord


case class Player(divisionName: String, name: String, country: String, wins: Int, draws: Int, losses: Int, matchesPlayed: Int, score: Int, matches: Option[List[Match]]) extends Ordered[Player] {
  override def compare(that: Player): Int = {
    if (this.score > that.score) {
      -1
    } else if (this.score < that.score) {
      1
    } else if (this.matchesPlayed > that.matchesPlayed) {
      -1
    } else if (this.matchesPlayed < that.matchesPlayed) {
      1
    } else {
      this.name.compare(that.name)
    }
  }
}



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

  def sortByScore(p1: Player, p2: Player): Boolean = p1.score > p2.score

}