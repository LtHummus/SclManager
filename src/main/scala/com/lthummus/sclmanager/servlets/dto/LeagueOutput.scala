package com.lthummus.sclmanager.servlets.dto

import zzz.generated.tables.records.DivisionRecord

case class LeagueList(leagues: List[League])

case class League(name: String, players: Option[List[Player]], precedence: Int, secret: Boolean) extends Ordered[League] {
  override def compare(that: League): Int = this.precedence.compareTo(that.precedence)
}

object League {
  def fromDatabaseRecord(record: DivisionRecord) = League(record.getName, None, record.getPrecedence, record.getSecret)
  def fromDatabaseRecord(record: DivisionRecord, players: List[Player]): League = {
    League(record.getName, Some(players.sorted), record.getPrecedence, record.getSecret)
  }
}

case class LeagueOverview(list: LeagueList)

