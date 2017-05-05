package com.lthummus.sclmanager.servlets.dto

import zzz.generated.tables.records.{DivisionRecord, PlayerRecord}

case class LeagueList(leagues: List[League])

case class League(name: String, players: Option[List[Player]], precedence: Int) extends Ordered[League] {
  override def compare(that: League): Int = this.precedence.compareTo(that.precedence)
}

object League {
  def fromDatabaseRecord(record: DivisionRecord) = League(record.getName, None, record.getPrecedence)
  def fromDatabaseRecord(record: DivisionRecord, players: List[PlayerRecord]) = {
    League(record.getName, Some(players.map(Player.fromDatabaseRecord(_)).sorted), record.getPrecedence)
  }
}

case class LeagueOverview(list: LeagueList)

