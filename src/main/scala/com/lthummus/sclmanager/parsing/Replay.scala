package com.lthummus.sclmanager.parsing


import java.io.{DataInputStream, InputStream}
import java.nio.{ByteBuffer, ByteOrder}
import java.util
import java.util.Date

import com.lthummus.sclmanager.parsing.GameLoadoutType.GameLoadoutType
import com.lthummus.sclmanager.parsing.GameResult.GameResult

import scalaz._
import Scalaz._

object GameResult extends Enumeration {
  type GameResult = Value
  val MissionWin, SpyTimeout, SpyShot, CivilianShot, InProgress = Value

  def fromInt(value: Int) = {
    value match {
      case 0 => MissionWin.right
      case 1 => SpyTimeout.right
      case 2 => SpyShot.right
      case 3 => CivilianShot.right
      case 4 => InProgress.right
      case _ => s"Unknown game result type: $value".left
    }
  }
}

object GameLoadoutType extends Enumeration {
  type GameLoadoutType = Value
  val Known, Any, Pick = Value

  def fromInt(value: Int) = {
    value match {
      case 0 => Known.right
      case 1 => Pick.right
      case 2 => Any.right
      case _ => s"Invalid game type: $value".left
    }
  }
}

case class GameType(kind: GameLoadoutType, x: Int, y: Int) {
  override def toString = kind match {
    case GameLoadoutType.Known => s"k$x"
    case GameLoadoutType.Any => s"a$x/$y"
    case GameLoadoutType.Pick => s"p$x/$y"
  }
}

object GameType {
  def fromInt(value: Int) = {
    val mode = value >> 28
    val y = (value & 0x0FFFC000) >> 14
    val x = value & 0x00003FFF

    for {
      gameType <- GameLoadoutType.fromInt(mode)
    } yield GameType(gameType, x, y)

  }
}

case class Replay(spy: String,
                  sniper: String,
                  sequence: Int,
                  result: GameResult,
                  level: Level,
                  loadoutType: GameType) extends Ordered[Replay] {
  override def compare(that: Replay): Int = this.sequence - that.sequence

  def isCompleted = result != GameResult.InProgress
  def spyWon = result == GameResult.CivilianShot || result == GameResult.MissionWin
  def sniperWon = result == GameResult.SpyShot || result == GameResult.SpyTimeout

  def winnerName = if (spyWon) spy else sniper
  def winnerRole = if (spyWon) "spy" else "sniper"

  def fullLevelName = s"${level.name} $loadoutType"
}

object Replay {

  val HeaderDataSizeBytes = 144

  private def extractInt(data: Array[Byte], index: Int) = {
    val buffer = ByteBuffer.wrap(data.slice(index, index + 4))
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    buffer.getInt
  }

  private def verifyMagicNumber(data: Array[Byte]): String \/ String = {
    if (data(0) == 'R' && data(1) == 'P' && data(2) == 'L' && data(3) == 'Y')
      "Magic Number OK".right
    else
      "Magic number incorrect".left
  }

  private def extractSpyNameLength(headerData: Array[Byte]) = {
    headerData(0x2E).right
  }

  private def extractSniperNameLength(headerData: Array[Byte]) = {
    headerData(0x2F).right
  }

  private def extractGameResult(headerData: Array[Byte]): String \/ GameResult = {
    for {
      gameResult <- GameResult.fromInt(headerData(0x30))
    } yield gameResult
  }

  private def extractSequenceNumber(headerData: Array[Byte]) = {
    headerData(0x2C).toInt.right
  }

  private def extractSpyName(headerData: Array[Byte], spyNameLength: Int) = {
    new String(headerData.slice(0x50, 0x50 + spyNameLength)).right
  }
  private def extractSniperName(headerData: Array[Byte], spyNameLength: Int, sniperNameLength: Int) = {
    new String(headerData.slice(0x50 + spyNameLength, 0x50 + spyNameLength + sniperNameLength)).right
  }

  private def extractLevel(headerData: Array[Byte]): String \/ Level = {
    val levelId = extractInt(headerData, 0x38)

    val levelObj = Level.AllLevels.filter(_.checksum == levelId)

    levelObj.length match {
      case 0 => s"No level found with id $levelId".left
      case 1 => levelObj.head.right
      case _ => s"Multiple levels found with id $levelId".left
    }
  }

  def fromInputStream(is: DataInputStream): String \/ Replay = {
    val headerData = new Array[Byte](HeaderDataSizeBytes)

    val bytesRead = is.read(headerData)

    if (bytesRead != HeaderDataSizeBytes) {
      return "Could not read entire replay data header".left
    }

    for {
      magicNumber <- verifyMagicNumber(headerData)
      spyNameLength <- extractSpyNameLength(headerData)
      sniperNameLength <- extractSniperNameLength(headerData)
      gameResult <- extractGameResult(headerData)
      sequenceNumber <- extractSequenceNumber(headerData)
      spy <- extractSpyName(headerData, spyNameLength)
      sniper <- extractSniperName(headerData, spyNameLength, sniperNameLength)
      gameType <- GameType.fromInt(extractInt(headerData, 0x34))
      level <- extractLevel(headerData)
    } yield Replay(spy, sniper, sequenceNumber, gameResult, level, gameType)
  }
}