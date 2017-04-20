package com.lthummus.sclmanager.parsing

import java.io.{ByteArrayInputStream, DataInputStream, InputStream}
import java.util.zip.{ZipEntry, ZipInputStream}

import com.lthummus.sclmanager.parsing.BoutTypeEnum.BoutType

import scalaz._
import Scalaz._
import scala.util.Try


object SpyPartyZipParser {

  def parseZipStream(bytes: Array[Byte]): String \/ List[Replay] = {
    val zis = new ZipInputStream(new ByteArrayInputStream(bytes))

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

    try {
      if (replays.isEmpty)
        "No replays found in ZIP file".left
      else
        replays.toList.right
    } catch {
      case e: Exception => e.getMessage.left
    }
  }
}
