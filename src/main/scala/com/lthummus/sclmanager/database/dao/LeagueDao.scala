package com.lthummus.sclmanager.database.dao

import com.lthummus.sclmanager.database.data.League
import org.jooq.DSLContext
import zzz.generated.Tables

import scala.collection.JavaConversions._

object LeagueDao {

  def all()(implicit dslContext: DSLContext): List[League] = {
    dslContext.selectFrom(Tables.LEAGUE).fetch.toList.map(League.fromDatabaseRecord)
  }
}
