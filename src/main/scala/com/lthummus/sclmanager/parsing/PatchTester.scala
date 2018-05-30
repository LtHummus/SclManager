package com.lthummus.sclmanager.parsing

import java.nio.file.{Files, Paths}

import org.apache.commons.io.IOUtils
import scalaz.{-\/, \/-}

object PatchTester extends App {

  val oldZip = "G:\\v5test.zip"

  val oldZipData = Files.readAllBytes(Paths.get(oldZip))

  val nameChangeMap = Map(
    "checker/thisisalongusername/test" -> "checker/thisisalongusername/test",
    "s76561197995390971/steam" -> "foobar"
  )

  ZipFilePatcher.patchZipFile(oldZipData, nameChangeMap) match {
    case -\/(error) => println(s"Error: $error")
    case \/-(newData) =>
      val replays = SpyPartyZipParser.parseZipStream(newData)
      replays.getOrElse(throw new IllegalStateException("whoops")).foreach(println)
  }

}
