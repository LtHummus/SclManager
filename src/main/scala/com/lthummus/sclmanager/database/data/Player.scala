package com.lthummus.sclmanager.database.data

import zzz.generated.tables.records.PlayerRecord


case class Player(id: Int, name: String, league: Int)

object Player {
  def fromDatabaseRecord(record: PlayerRecord) = {
    Player(record.getId, record.getName, record.getLeague)
  }
}