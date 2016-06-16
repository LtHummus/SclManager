package com.lthummus.sclmanager.parsing

import java.io.{DataInputStream, InputStream}
import java.util.zip.{ZipEntry, ZipInputStream}

import scalaz._
import Scalaz._


object SpyPartyZipParser {

  def parseZipStream(is: InputStream): String \/ Match = {
    val zis = new ZipInputStream(is)

    val replays = scala.collection.mutable.ListBuffer[Replay]()
    var entry: ZipEntry = zis.getNextEntry

    while (entry != null) {
      if (!entry.isDirectory) {
        val parsed = Replay.fromInputStream(new DataInputStream(zis))
        parsed match {
          case -\/(msg) => return ("Could not parse replay: " + msg).left
          case \/-(replay) => replays += replay
        }
      }
      entry = zis.getNextEntry
    }

    zis.close()

    Match(replays).right
  }
}
