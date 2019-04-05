package com.lthummus.sclmanager.servlets.dto

import zzz.generated.tables.records.{DivisionRecord, PlayerRecord}

case class SimplePlayer(name: String, country: String)

object SimplePlayer {
  def fromPlayer(record: Player): SimplePlayer = {
    SimplePlayer(record.name, record.country)
  }
}

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

  def asSimplePlayer: SimplePlayer = SimplePlayer(name, country)
  def withMatches(matches: Option[List[Match]]): Player = copy(matches = matches)

  //we always define the list here...I don't _think_ that will break anything
  def sanitize: Player = copy(wins = 0, losses = 0, draws = 0, score = 0, matches = Some(List()))
}



object Player {

  def fromDatabaseRecord(playerRecord: PlayerRecord, divisionRecord: DivisionRecord, matches: Option[List[Match]] = None): Player = {
    val matchesPlayed = playerRecord.getWins + playerRecord.getLosses + playerRecord.getDraws
    val score = playerRecord.getWins * divisionRecord.getWinPoints +
      playerRecord.getDraws * divisionRecord.getDrawPoints +
      playerRecord.getLosses * divisionRecord.getLossPoints

    Player(playerRecord.getDivision,
      playerRecord.getName,
      playerRecord.getCountry,
      playerRecord.getWins,
      playerRecord.getDraws,
      playerRecord.getLosses,
      matchesPlayed,
      score,
      matches)
  }



  def sortByScore(p1: Player, p2: Player): Boolean = p1.score > p2.score

}