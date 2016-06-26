package com.lthummus.sclmanager.servlets.dto

import zzz.generated.tables.records.{LeagueRecord, PlayerRecord}

case class LeagueList(leagues: List[League])

case class League(id: Int, name: String, players: Option[List[Player]])

object League {
  def fromDatabaseRecord(record: LeagueRecord) = League(record.getId, record.getName, None)
  def fromDatabaseRecord(record: LeagueRecord, players: List[PlayerRecord]) = {
    League(record.getId, record.getName, Some(players.map(Player.fromDatabaseRecord(_))))
  }
}

