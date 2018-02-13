package com.lthummus.sclmanager.parsing

import java.io.File
import java.nio.file.{Files, Paths}


import scalaz._
import Scalaz._

object TestParser extends App {

  val FilePath = "G:\\tunewins.zip"

  val bytes = Files.readAllBytes(Paths.get(FilePath))

  val result = SpyPartyZipParser.parseZipStream(bytes)

  result match {
    case -\/(error)   => s"Error parsing: $error"
    case \/-(replays) => replays.foreach(println)
      val b = Bout(replays, BoutTypeEnum.Standard)
      println(b.isTie)
      println(b.getGameSummary.mkString("\n"))
      println(b.getScoreLine)
      println(b.result("lthummus"))
      println(b.result("aforgottentune"))
  }
}
