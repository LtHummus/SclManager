package com.lthummus.sclmanager.database.dao

import com.lthummus.sclmanager.servlets.dto.MapOfTheWeekEntry
import org.jooq.DSLContext
import zzz.generated.Tables

import scala.collection.JavaConversions._


object MapOfTheWeekDao {

  def all()(implicit dslContext: DSLContext): List[MapOfTheWeekEntry] = {
    dslContext
      .selectFrom(Tables.HOME_MAPS)
      .fetch()
      .toList
      .map(MapOfTheWeekEntry(_))
  }

  def getByWeek(week: Int)(implicit dslContext: DSLContext): Option[MapOfTheWeekEntry] = {
    val record = dslContext
      .selectFrom(Tables.HOME_MAPS)
      .where(Tables.HOME_MAPS.WEEK.eq(week))
      .fetchOne()

    Option(record).map(MapOfTheWeekEntry(_))
  }
}
