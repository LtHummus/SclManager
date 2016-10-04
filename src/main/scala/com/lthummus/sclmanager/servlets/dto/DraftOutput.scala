package com.lthummus.sclmanager.servlets.dto

import java.sql.Timestamp

import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import zzz.generated.tables.records.DraftRecord


case class Draft(id: Int, roomCode: String, player1: String, player2: String, time: Timestamp, payload: DraftPayload) {
  def asForumPost: String = {
    val banString = payload.bannedMaps.map(it => s"\t${it.picker} has banned ${it.map}").mkString("\n")
    val pickString = payload.pickedMaps.map(it => s"\t${it.picker} has picked ${it.map}").mkString("\n")
    s"""
      |BANS:
      |$banString
      |
      |PICKS:
      |$pickString
    """.stripMargin
  }
}

object Draft {
  implicit val formats = DefaultFormats

  def fromDatabaseRecord(record: DraftRecord) = {
    Draft(record.getId,
      record.getRoomCode,
      record.getPlayer1,
      record.getPlayer2,
      record.getTime,
      parse(record.getPayload).extract[DraftPayload])
  }
}
