package com.lthummus.sclmanager.parsing

import java.io.File
import java.nio.file.{Files, Paths}

import scalaz._
import Scalaz._

object TestParser extends App {

  val FilePath = "E:\\steph.zip"

  val bytes = Files.readAllBytes(Paths.get(FilePath))

  val result = SpyPartyZipParser.parseZipStream(bytes)

  result match {
    case -\/(error) => s"Something done fucked up: $error"
    case \/-(parsed) =>
      parsed.orderedReplays.foreach(println(_))
  }
}
