package com.lthummus.sclmanager.parsing

import java.io.File
import java.nio.file.{Files, Paths}

import scalaz._
import Scalaz._

object TestParser extends App {

  val FilePath = "C:\\Users\\Benjamin\\Downloads\\SpyPartyReplay-20160609-18-03-46-cameraman-vs-warningtrack-1tZv_xHrSDCQLhXyUlfAEg-v18.zip"

  val bytes = Files.readAllBytes(Paths.get(FilePath))

  val result = SpyPartyZipParser.parseZipStream(bytes)

  println(result)
}
