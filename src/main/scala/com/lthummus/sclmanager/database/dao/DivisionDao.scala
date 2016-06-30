package com.lthummus.sclmanager.database.dao

import org.jooq.DSLContext
import zzz.generated.Tables
import zzz.generated.tables.records.DivisionRecord

import scala.collection.JavaConversions._


object DivisionDao {

  def all()(implicit dslContext: DSLContext): List[DivisionRecord] = {
    dslContext.selectFrom(Tables.DIVISION).fetch().toList
  }

  def getByName(name: String)(implicit dslContext: DSLContext): Option[DivisionRecord] = {
    val res = dslContext.selectFrom(Tables.DIVISION).where(Tables.DIVISION.NAME.eq(name)).fetch()

    res.size() match {
      case 0 => None
      case _ => Some(res(0))
    }
  }

  def getPlayersInLeague(name: String)(implicit dslContext: DSLContext) = {
    dslContext.selectFrom(Tables.PLAYER).where(Tables.PLAYER.DIVISION.eq(name)).fetch().toList
  }
}
