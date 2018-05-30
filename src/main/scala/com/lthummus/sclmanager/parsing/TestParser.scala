package com.lthummus.sclmanager.parsing

import java.io.File
import java.nio.file.{Files, Paths}


import scalaz._
import Scalaz._

object TestParser extends App {

  val FilePath = "G:\\lthummus vs s76561198016757660%2fsteam - 20180429-13-13-34.zip"

  val bytes = Files.readAllBytes(Paths.get(FilePath))

  val result = SpyPartyZipParser.parseZipStream(bytes)

  result match {
    case -\/(error)   => println(s"Error parsing: $error")
    case \/-(replays) =>
      replays.foreach(println)
  }
}
