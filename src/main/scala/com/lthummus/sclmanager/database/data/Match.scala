package com.lthummus.sclmanager.database.data

import zzz.generated.tables.records.MatchRecord


case class Match(id: Int, week: Int, league: Int, player1: Int, player2: Int, status: Int) extends Ordered[Match] {
  override def compare(that: Match): Int = this.week - that.week
}

object Match {
  def fromDatabaseObject(record: MatchRecord) = {
    Match(record.getId, record.getWeek, record.getLeague, record.getPlayer1, record.getPlayer2, record.getStatus)
  }
}
