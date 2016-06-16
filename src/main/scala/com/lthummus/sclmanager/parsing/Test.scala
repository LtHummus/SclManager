package com.lthummus.sclmanager.parsing

import java.io.{DataInputStream, FileInputStream}
import java.util.zip.{ZipEntry, ZipInputStream}

import scalaz.{-\/, \/-}


object Test extends App {

  val zipPath = "F:\\SpyPartyReplay-20160611-17-03-00-krazycaley-vs-magician1099-QFaOf6HZRFubm3LBicu27Q-v18.zip"

  val zis = new ZipInputStream(new FileInputStream(zipPath))

  val replays = scala.collection.mutable.ListBuffer[Replay]()
  var entry: ZipEntry = zis.getNextEntry

  while (entry != null) {
    if (!entry.isDirectory) {
      val parsed = Replay.fromInputStream(new DataInputStream(zis))
      parsed match {
        case -\/(msg) => println("Could not parse replay: " + msg)
        case \/-(replay) => replays += replay
      }
    }
    entry = zis.getNextEntry
  }

  zis.close()

  val m = Match(replays)

  println(m.getScoreLine)

  println()
  println(m.getGameSummary mkString "\n")


}
