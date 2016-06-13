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
      case 0 => MissionWin
      case 1 => SpyTimeout
      case 2 => SpyShot
      case 3 => CivilianShot
      case 4 => InProgress
      case _ => throw new IllegalArgumentException(s"Unknown game result type: $value")
    }
  }
}

object GameLoadoutType extends Enumeration {
  type GameLoadoutType = Value
  val Known, Any, Pick = Value

  def fromInt(value: Int) = {
    value match {
      case 0 => Known
      case 1 => Pick
      case 2 => Any
      case _ => throw new IllegalArgumentException(s"Unknown game mode type: $value")
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
    GameType(GameLoadoutType.fromInt(mode), x, y)
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

  private def verifyMagicNumber(data: Array[Byte]) =
    data(0) == 'R' &&
    data(1) == 'P' &&
    data(2) == 'L' &&
    data(3) == 'Y'

  private def extractInt(data: Array[Byte], index: Int) = {
    val buffer = ByteBuffer.wrap(data.slice(index, index + 4))
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    buffer.getInt
  }
  private def extractShort(data: Array[Byte]) = ByteBuffer.wrap(data).getShort


  def fromInputStream(is: DataInputStream): String \/ Replay = {
    val headerData = new Array[Byte](HeaderDataSizeBytes)

    val bytesRead = is.read(headerData)

    if (bytesRead != HeaderDataSizeBytes) {
      return "Could not read entire replay data header".left
    }


    val spyNameLength = headerData(0x2E).toInt
    val sniperNameLength = headerData(0x2F).toInt

    val result = GameResult.fromInt(headerData(0x30))

    val sequenceNumber = headerData(0x2C).toInt


    val spy = new String(headerData.slice(0x50, 0x50 + spyNameLength))
    val sniper = new String(headerData.slice(0x50 + spyNameLength, 0x50 + spyNameLength + sniperNameLength))


    val packedLoadout = extractInt(headerData, 0x34)
    val gameType = GameType.fromInt(packedLoadout)

    val levelId = extractInt(headerData, 0x38)

    val levelObj = Level.AllLevels.filter(_.checksum == levelId)

    Replay(spy, sniper, sequenceNumber, result, levelObj.head, gameType).right
  }
}