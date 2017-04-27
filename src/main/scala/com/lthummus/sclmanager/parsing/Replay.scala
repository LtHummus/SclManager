package com.lthummus.sclmanager.parsing


import java.io.{DataInputStream, InputStream}
import java.nio.{ByteBuffer, ByteOrder}
import java.util
import java.util.{Base64, Date}

import org.joda.time.DateTime

import scalaz._
import Scalaz._

object GameResultEnum {
  sealed abstract class GameResult(val niceName: String, val internalId: Int) {
    override def toString: String = niceName
  }

  case object MissionWin extends GameResult("Mission Win", 0)
  case object SpyTimeout extends GameResult("Spy Timeout", 1)
  case object SpyShot extends GameResult("Spy Shot", 2)
  case object CivilianShot extends GameResult("Civilian Shot", 3)
  case object InProgress extends GameResult("In Progress", 4)

  def fromString(value: String): String \/ GameResult = {
    value match {
      case "Mission Win"   => MissionWin.right
      case "Spy Timeout"   => SpyTimeout.right
      case "Spy Shot"      => SpyShot.right
      case "Civilian Shot" => CivilianShot.right
      case "In Progress"   => InProgress.right
      case _              => s"Unknown game result type: $value".left
    }
  }

  def fromInt(value: Int): String \/ GameResult = {
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

import GameResultEnum._
import GameLoadoutTypeEnum._

object GameLoadoutTypeEnum {
  sealed abstract class GameLoadoutType(shortName: String)

  case object Known extends GameLoadoutType("k")
  case object Pick extends GameLoadoutType("p")
  case object Any extends GameLoadoutType("a")

  def fromInt(value: Int): String \/ GameLoadoutType = {
    value match {
      case 0 => Known.right
      case 1 => Pick.right
      case 2 => Any.right
      case _ => s"Invalid game type: $value".left
    }
  }
}

object GameType {
  def fromString(value: String): String \/ GameType = {
    val kind = value.charAt(0)
    val x = value.charAt(1) - 0x30

    kind match {
      case 'k' => GameType(GameLoadoutTypeEnum.Known, x, 0).right
      case 'a' => GameType(GameLoadoutTypeEnum.Any, x, value.charAt(3) - 0x30).right
      case 'p' => GameType(GameLoadoutTypeEnum.Pick, x, value.charAt(3) - 0x30).right
      case _ => "Unknown game type format".left
    }
  }

  def fromInt(value: Int): String \/ GameType = {
    val mode = value >> 28
    val y = (value & 0x0FFFC000) >> 14
    val x = value & 0x00003FFF

    for {
      gameType <- GameLoadoutTypeEnum.fromInt(mode)
    } yield GameType(gameType, x, y)
  }
}



case class GameType(kind: GameLoadoutType, x: Int, y: Int) {
  override def toString = kind match {
    case GameLoadoutTypeEnum.Known => s"k$x"
    case GameLoadoutTypeEnum.Any => s"a$x/$y"
    case GameLoadoutTypeEnum.Pick => s"p$x/$y"
  }
}



case class Replay(spy: String,
                  sniper: String,
                  startTime: DateTime,
                  result: GameResult,
                  level: Level,
                  loadoutType: GameType,
                  sequenceNumber: Int,
                  uuid: String) extends Ordered[Replay] {
  override def compare(that: Replay): Int = if (this.startTime.isBefore(that.startTime)) -1 else 1

  def isCompleted: Boolean = result != GameResultEnum.InProgress
  def spyWon: Boolean = result == GameResultEnum.CivilianShot || result == GameResultEnum.MissionWin
  def sniperWon: Boolean = result == GameResultEnum.SpyShot || result == GameResultEnum.SpyTimeout

  def winnerName: String = if (spyWon) spy else sniper
  def winnerRole: String = if (spyWon) "spy" else "sniper"

  def fullLevelName = s"${level.name} $loadoutType"

  def description: String = s"$winnerName won as $winnerRole on $fullLevelName"
}

object Replay {

  val HeaderDataSizeBytes = 144

  private def extractInt(data: Array[Byte], index: Int) = {
    val buffer = ByteBuffer.wrap(data.slice(index, index + 4))
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    buffer.getInt
  }

  private def extractShort(data: Array[Byte], index: Int) = {
    val buffer = ByteBuffer.wrap(data.slice(index, index + 2))
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    buffer.getShort
  }

  private def verifyMagicNumber(data: Array[Byte]): String \/ String = {
    if (data(0) == 'R' && data(1) == 'P' && data(2) == 'L' && data(3) == 'Y')
      "Magic Number OK".right
    else
      "Magic number incorrect".left
  }

  private def verifyFileHeaderVersion(data: Byte): String \/ String = {
    if (data == 0x03)
      "File header version OK".right
    else
      "File header version not understood".left
  }

  private def extractSpyNameLength(headerData: Array[Byte]) = {
    headerData(0x2E).right
  }

  private def extractSniperNameLength(headerData: Array[Byte]) = {
    headerData(0x2F).right
  }

  private def extractGameResult(headerData: Array[Byte]): String \/ GameResult = {
    for {
      gameResult <- GameResultEnum.fromInt(headerData(0x30))
    } yield gameResult
  }

  private def extractStartTime(headerData: Array[Byte]) = {
    val timeBytes = headerData.slice(0x28, 0x2C)
    val buffer = ByteBuffer.wrap(timeBytes)
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    val res = buffer.getInt() & 0xFFFFFFFFL

    new DateTime(res * 1000).right
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

  private def extractSequenceNumber(headerData: Array[Byte]): String \/ Int = {
    extractShort(headerData, 0x2C).toInt.right
  }

  private def extractUuid(headerData: Array[Byte]): String \/ String = {
    Base64
      .getEncoder
      .encodeToString(headerData.slice(0x18, 0x18 + 16)) //encode bytes to base64 string
      .split("=")(0)                                     //drop trailing = signs
      .replaceAll("\\+", "-")                            //replace + with - because that's how spyparty does things
      .replaceAll("/", "_")                              //replace / with _ ibid
      .right
  }

  def fromInputStream(is: DataInputStream): String \/ Replay = {
    val headerData = new Array[Byte](HeaderDataSizeBytes)

    val bytesRead = is.read(headerData)

    if (bytesRead != HeaderDataSizeBytes) {
      return "Could not read entire replay data header".left
    }

    for {
      _ <- verifyMagicNumber(headerData)
      _ <- verifyFileHeaderVersion(headerData(4))
      spyNameLength <- extractSpyNameLength(headerData)
      sniperNameLength <- extractSniperNameLength(headerData)
      gameResult <- extractGameResult(headerData)
      startTime <- extractStartTime(headerData)
      spy <- extractSpyName(headerData, spyNameLength)
      sniper <- extractSniperName(headerData, spyNameLength, sniperNameLength)
      gameType <- GameType.fromInt(extractInt(headerData, 0x34))
      level <- extractLevel(headerData)
      sequence <- extractSequenceNumber(headerData)
      uuid <- extractUuid(headerData)
    } yield Replay(spy, sniper, startTime, gameResult, level, gameType, sequence, uuid)
  }
}