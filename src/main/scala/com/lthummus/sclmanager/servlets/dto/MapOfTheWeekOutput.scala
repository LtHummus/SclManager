package com.lthummus.sclmanager.servlets.dto

import zzz.generated.tables.records.HomeMapsRecord


case class MapOfTheWeekEntry(week: Int, level: String, loadout: String, picker: String)

object MapOfTheWeekEntry {
  def apply(record: HomeMapsRecord): MapOfTheWeekEntry = {
    MapOfTheWeekEntry(record.getWeek, record.getLevel, record.getLoadout, record.getPicker)
  }

  def defaultForWeek(week: Int): MapOfTheWeekEntry = {
    MapOfTheWeekEntry(week, "Unknown", "Unknown", "No one")
  }
}


case class MapOfTheWeekData(homeMaps: List[MapOfTheWeekEntry])

