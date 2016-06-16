package com.lthummus.sclmanager.database.dao

import com.lthummus.sclmanager.database.data.League
import org.jooq.DSLContext
import zzz.generated.Tables

import scala.collection.JavaConversions._

object LeagueDao {

  def all()(implicit dslContext: DSLContext): List[League] = {
    dslContext.selectFrom(Tables.LEAGUE).fetch.toList.map(League.fromDatabaseRecord)
  }

  def getById(id: Int)(implicit dslContext: DSLContext): Option[League] = {
    val res = dslContext.selectFrom(Tables.LEAGUE).where(Tables.LEAGUE.ID.eq(id)).fetch()

    res.size() match {
      case 0 => None
      case _ => Some(League.fromDatabaseRecord(res.get(0)))
    }
  }
}
