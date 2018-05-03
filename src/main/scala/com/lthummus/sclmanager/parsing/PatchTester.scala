package com.lthummus.sclmanager.parsing

import java.io.FileOutputStream
import java.nio.file.{Files, Paths}

import org.apache.commons.io.IOUtils
import scalaz.{-\/, \/-}

object PatchTester extends App {

  val oldZip = "C:\\Users\\Benjamin\\AppData\\Local\\SpyParty\\replays\\Matches\\2018-04\\lthummus vs s76561198016757660%2fsteam - 20180429-13-13-34\\test.zip"

  val oldZipData = Files.readAllBytes(Paths.get(oldZip))

  val nameChangeMap = Map(
    "lthummus" -> "abcdefg",
    "s76561198016757660/steam" -> "DPWSEY"
  )

  ZipFilePatcher.patchZipFile(oldZipData, nameChangeMap) match {
    case -\/(error) => s"Error: $error"
    case \/-(newData) =>
      println("TODO: Write")
  }

}
