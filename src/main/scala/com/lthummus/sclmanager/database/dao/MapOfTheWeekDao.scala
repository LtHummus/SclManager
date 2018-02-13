package com.lthummus.sclmanager.database.dao

object MapOfTheWeekDao {

  case class MapOfTheWeekEntry(week: Int, level: String, loadout: String, picker: String)


  def all(): List[MapOfTheWeekEntry] = {
    List(
      MapOfTheWeekEntry(1, "Courtyard 2", "Any 4/7", "LtHummus"),
      MapOfTheWeekEntry(2, "Pub", "Any 3/5; Fingerprint Required", "LtHummus")
    )
  }
}
