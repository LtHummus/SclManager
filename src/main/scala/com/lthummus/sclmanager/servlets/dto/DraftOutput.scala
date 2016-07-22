package com.lthummus.sclmanager.servlets.dto

import java.sql.Timestamp

import zzz.generated.tables.records.DraftRecord


case class Draft(id: Int, roomCode: String, player1: String, player2: String, time: Timestamp, playload: String)

object Draft {
  def fromDatabaseRecord(record: DraftRecord) = {
    Draft(record.getId,
      record.getRoomCode,
      record.getPlayer1,
      record.getPlayer2,
      record.getTime,
      record.getPayload)
  }
}
