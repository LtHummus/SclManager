package com.lthummus.sclmanager.database.dao

import com.lthummus.sclmanager.servlets.dto.Player
import org.jooq.DSLContext
import zzz.generated.Tables
import zzz.generated.tables.records.{DivisionRecord, PlayerRecord}

import scala.collection.JavaConverters._


object DivisionDao {

  implicit class RichPlayerRecord(x: PlayerRecord) {
    def isActive: Boolean = x.getActive != 0x00.toByte
    def isParticipating: Boolean = x.getParticipating != 0x00.toByte
  }

  def all()(implicit dslContext: DSLContext): List[DivisionRecord] = {
    dslContext.selectFrom(Tables.DIVISION).fetch().asScala.toList
  }

  def getByName(name: String)(implicit dslContext: DSLContext): Option[DivisionRecord] = {
    val res = dslContext.selectFrom(Tables.DIVISION).where(Tables.DIVISION.NAME.eq(name)).fetch()

    res.size() match {
      case 0 => None
      case _ => Some(res.get(0))
    }
  }

  def getParticipatingPlayersInDivision(name: String)(implicit dslContext: DSLContext): List[PlayerRecord] = {
    dslContext
      .selectFrom(Tables.PLAYER)
      .where(Tables.PLAYER.DIVISION.eq(name))
      .and(Tables.PLAYER.PARTICIPATING.ne(0x00.toByte))
      .fetch()
      .asScala
      .toList
  }
}
