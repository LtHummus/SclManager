package com.lthummus.sclmanager.servlets.dto

case class NewMatchesInput(week: Int, matches: String, division: Option[String]) {

  def matchPairs: List[(String, String)] = {
    matches.lines.toList.map{ l =>
      val parts = l.split(",")
      (parts(0).trim, parts(1).trim)
    }
  }

  def effectiveDivision: String = division.getOrElse("Challenger")


}