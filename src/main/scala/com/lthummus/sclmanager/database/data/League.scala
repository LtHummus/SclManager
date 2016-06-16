package com.lthummus.sclmanager.database.data

import zzz.generated.tables.records.LeagueRecord

case class League(id: Int, name: String)

object League {
  def fromDatabaseRecord(record: LeagueRecord) = {
    League(record.getId, record.getName)
  }
}