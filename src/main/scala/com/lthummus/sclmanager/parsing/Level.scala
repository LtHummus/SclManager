package com.lthummus.sclmanager.parsing


case class Level(name: String, checksum: Int)

object Level {
  def AllLevels = Seq(Level("Ballroom", 12345))
}
