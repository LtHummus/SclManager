package com.lthummus.sclmanager.servlets.dto

case class NewMatchesInput(week: Int, matches: String, division: Option[String], password: String) {

  def matchPairs: List[(String, String)] = {
    matches.lines.toList.flatMap{ l =>
      val parts = l.split(",").map(_.trim).sorted
      if (parts(0) != "BYE" && parts(1) != "BYE")
        Some((parts(0).trim, parts(1).trim))
      else
        None
    }
  }

  def effectiveDivision: String = division.getOrElse("Challenger")


}