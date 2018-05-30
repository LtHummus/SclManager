package com.lthummus.sclmanager.parsing

import java.io.{ByteArrayInputStream, DataInputStream}

import scalaz._
import Scalaz._
import org.apache.commons.io.IOUtils

object ReplayNamePatcher {

  private def doActualPatch(replayData: Array[Byte], headerData: Replay, nameChanges: Map[String, String]): String \/ Array[Byte] = {
    if (nameChanges.forall { case (a, b) => a == b }) {
      replayData.right
    } else if (replayData(4) == 0x04) {
      version4Patch(replayData, headerData, nameChanges)
    } else if (replayData(4) == 0x05) {
      version5Patch(replayData, headerData, nameChanges)
    } else {
      val versionNumber = replayData(4)
      s"I don't know how to patch version $versionNumber replays".left
    }
  }

  //So i'm not proud of this, BUTTTTTT I've already got everyone's usernames, so we're going to just ignore display name
  //for now, and pretend it doesn't exist. For season 5 of SCL, we should do everything with _ACTUAL_ steam names.
  //
  //checker, if you are reading this, i am sorry :(
  private def version5Patch(replayData: Array[Byte], headerData: Replay, nameChanges: Map[String, String]) = {
    val newReplayLength = replayData.length -
      (headerData.sniper.length + headerData.spy.length) +
      (nameChanges(headerData.spy).length + nameChanges(headerData.sniper).length)

    val newReplayBytes = Array.fill[Byte](newReplayLength)(0x00)

    //copy everything up until name lengths to the new array
    System.arraycopy(replayData, 0, newReplayBytes, 0, 0x2E)

    val newSpyName = nameChanges.getOrElse(headerData.spy, headerData.spy)
    val newSniperName = nameChanges.getOrElse(headerData.sniper, headerData.sniper)

    newReplayBytes(0x2E) = newSpyName.length.toByte
    newReplayBytes(0x2F) = newSniperName.length.toByte

    //now, copy over the next stuff, until the names
    System.arraycopy(replayData, 0x30, newReplayBytes, 0x30, 0x30)

    //write the new names
    System.arraycopy(newSpyName.getBytes, 0, newReplayBytes, 0x60, newSpyName.length)
    System.arraycopy(newSniperName.getBytes, 0, newReplayBytes, 0x60 + newSpyName.length, newSniperName.length)

    val payloadAfterNames = replayData.length - (headerData.spy.length + headerData.sniper.length + 0x60)

    System.arraycopy(replayData, 0x60 + headerData.spy.length + headerData.sniper.length, newReplayBytes, 0x60 + newSpyName.length + newSniperName.length, payloadAfterNames)

    newReplayBytes.right

  }

  private def version4Patch(replayData: Array[Byte], headerData: Replay, nameChanges: Map[String, String]) = {
    val newReplayLength = replayData.length -
      (headerData.sniper.length + headerData.spy.length) +
      (nameChanges(headerData.spy).length + nameChanges(headerData.sniper).length)

    val newReplayBytes = Array.fill[Byte](newReplayLength)(0x00)

    //copy everything up until name lengths to the new array
    System.arraycopy(replayData, 0, newReplayBytes, 0, 0x2E)

    val newSpyName = nameChanges.getOrElse(headerData.spy, headerData.spy)
    val newSniperName = nameChanges.getOrElse(headerData.sniper, headerData.sniper)

    newReplayBytes(0x2E) = newSpyName.length.toByte
    newReplayBytes(0x2F) = newSniperName.length.toByte

    //now, copy over the next stuff, until the names
    System.arraycopy(replayData, 0x30, newReplayBytes, 0x30, 0x24)

    //now, write the new names
    System.arraycopy(newSpyName.getBytes, 0, newReplayBytes, 0x54, newSpyName.length)
    System.arraycopy(newSniperName.getBytes, 0, newReplayBytes, 0x54 + newSpyName.length, newSniperName.length)

    val payloadAfterNames = replayData.length - (headerData.spy.length + headerData.sniper.length + 0x54)

    //now, copy everything else over
    System.arraycopy(replayData, 0x54 + headerData.spy.length + headerData.sniper.length, newReplayBytes, 0x54 + newSpyName.length + newSniperName.length, payloadAfterNames)

    newReplayBytes.right
  }

  def patchReplay(replayFile: DataInputStream, nameChanges: Map[String, String]): String \/ Array[Byte] = {
    val replayBytes = IOUtils.toByteArray(replayFile)
    val newIS = new DataInputStream(new ByteArrayInputStream(replayBytes))

    Replay.fromInputStream(newIS) match {
      case -\/(error) => error.left
      case \/-(replay) => doActualPatch(replayBytes, replay, nameChanges)
    }

  }
}
