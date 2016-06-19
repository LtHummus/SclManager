package com.lthummus.sclmanager.database.dao

import org.jooq.DSLContext
import zzz.generated.Tables
import zzz.generated.tables.records.LeagueRecord

import scala.collection.JavaConversions._

object LeagueDao {

  def all()(implicit dslContext: DSLContext): List[LeagueRecord] = {
    dslContext.selectFrom(Tables.LEAGUE).fetch().toList
  }

  def getById(id: Int)(implicit dslContext: DSLContext): Option[LeagueRecord] = {
    val res = dslContext.selectFrom(Tables.LEAGUE).where(Tables.LEAGUE.ID.eq(id)).fetch()

    res.size() match {
      case 0 => None
      case _ => Some(res(0))
    }
  }
}
