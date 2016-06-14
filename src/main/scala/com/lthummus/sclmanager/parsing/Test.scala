package com.lthummus.sclmanager.parsing

import java.io.{DataInputStream, FileInputStream}
import java.util.zip.{ZipEntry, ZipInputStream}

import scalaz.{-\/, \/-}


object Test extends App {

  val zipPath = "E:\\magic.zip"

  val zis = new ZipInputStream(new FileInputStream(zipPath))

  val replays = scala.collection.mutable.ListBuffer[Replay]()
  var entry: ZipEntry = zis.getNextEntry

  while (entry != null) {
    if (!entry.isDirectory) {
      val parsed = Replay.fromInputStream(new DataInputStream(zis))
      parsed match {
        case -\/(_) => println("Could not parse replay: " + entry.getName)
        case \/-(replay) => replays += replay
      }
    }
    entry = zis.getNextEntry
  }

  zis.close()

  Match(replays)


}
