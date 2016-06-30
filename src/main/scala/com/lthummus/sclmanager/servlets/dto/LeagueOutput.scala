package com.lthummus.sclmanager.servlets.dto

import zzz.generated.tables.records.{DivisionRecord, PlayerRecord}

case class LeagueList(leagues: List[League])

case class League(name: String, players: Option[List[Player]])

object League {
  def fromDatabaseRecord(record: DivisionRecord) = League(record.getName, None)
  def fromDatabaseRecord(record: DivisionRecord, players: List[PlayerRecord]) = {
    League(record.getName, Some(players.map(Player.fromDatabaseRecord(_))))
  }
}

