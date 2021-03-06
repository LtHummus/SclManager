package com.lthummus.sclmanager.parsing


import java.io.{DataInputStream, InputStream}
import java.nio.charset.Charset
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
      case _               => s"Unknown game result type: $value".left
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
  override def toString: String = kind match {
    case GameLoadoutTypeEnum.Known => s"k$x"
    case GameLoadoutTypeEnum.Any => s"a$x/$y"
    case GameLoadoutTypeEnum.Pick => s"p$x/$y"
  }
}

sealed trait ReplayOffsets {
  val versionNumber: Int
  val hasPotentiallyMultipleNames: Boolean = false
  val hasExtraData: Boolean = false

  val magicNumberOffset: Int
  val fileVersionOffset: Int
  val protocolVersionOffset: Int
  val spyPartyVersionOffset: Int
  val durationOffset: Int
  val uuidOffset: Int
  val timestampOffset: Int
  val sequenceNumberOffset: Int
  val spyNameLengthOffset: Int
  val sniperNameLengthOffset: Int
  val gameResultOffset: Int
  val gameTypeOffset: Int
  val levelOffset: Int
  val playerNamesOffset: Int
  val missionsCompletedOffset: Int

  val spyDisplayNameLengthOffset: Option[Int] = None
  val sniperDisplayNameLength: Option[Int] = None
  val startDurationOffset: Option[Int] = None
  val numGuestsOffset: Option[Int] = None
}

object Version3ReplayOffsets extends ReplayOffsets {
  override val versionNumber: Int = 3

  override val magicNumberOffset: Int       = 0x00
  override val fileVersionOffset: Int       = 0x04
  override val protocolVersionOffset: Int   = 0x08
  override val spyPartyVersionOffset: Int   = 0x0C
  override val durationOffset: Int          = 0x14
  override val uuidOffset: Int              = 0x18
  override val timestampOffset: Int         = 0x28
  override val sequenceNumberOffset: Int    = 0x2C
  override val spyNameLengthOffset: Int     = 0x2E
  override val sniperNameLengthOffset: Int  = 0x2F
  override val gameResultOffset: Int        = 0x30
  override val gameTypeOffset: Int          = 0x34
  override val levelOffset: Int             = 0x38
  override val missionsCompletedOffset: Int = 0x44
  override val playerNamesOffset: Int       = 0x50
}

object Version4ReplayOffsets extends ReplayOffsets {
  override val versionNumber: Int = 4

  override val magicNumberOffset: Int       = 0x00
  override val fileVersionOffset: Int       = 0x04
  override val protocolVersionOffset: Int   = 0x08
  override val spyPartyVersionOffset: Int   = 0x0C
  override val durationOffset: Int          = 0x14
  override val uuidOffset: Int              = 0x18
  override val timestampOffset: Int         = 0x28
  override val sequenceNumberOffset: Int    = 0x2C
  override val spyNameLengthOffset: Int     = 0x2E
  override val sniperNameLengthOffset: Int  = 0x2F
  override val gameResultOffset: Int        = 0x34
  override val gameTypeOffset: Int          = 0x38
  override val levelOffset: Int             = 0x3C
  override val missionsCompletedOffset: Int = 0x48
  override val playerNamesOffset: Int       = 0x54
}

//this isn't the greatest
//@TODO: what we should really do here is put the actual methods for extracting the data in these objects,
//       so we don't have to mix logic together
object Version5ReplayOffsets extends ReplayOffsets {
  override val versionNumber: Int = 5
  override val hasPotentiallyMultipleNames: Boolean = true
  override val hasExtraData: Boolean = true

  override val magicNumberOffset: Int       = 0x00
  override val fileVersionOffset: Int       = 0x04
  override val protocolVersionOffset: Int   = 0x08
  override val spyPartyVersionOffset: Int   = 0x0C
  override val durationOffset: Int          = 0x14
  override val uuidOffset: Int              = 0x18
  override val timestampOffset: Int         = 0x28
  override val sequenceNumberOffset: Int    = 0x2C
  override val spyNameLengthOffset: Int     = 0x2E
  override val sniperNameLengthOffset: Int  = 0x2F
  override val gameResultOffset: Int        = 0x38
  override val gameTypeOffset: Int          = 0x3C
  override val levelOffset: Int             = 0x40
  override val missionsCompletedOffset: Int = 0x4C
  override val playerNamesOffset: Int       = 0x60

  override val spyDisplayNameLengthOffset: Option[Int] = Some(0x30)
  override val sniperDisplayNameLength: Option[Int]    = Some(0x31)
  override val startDurationOffset: Option[Int]        = Some(0x50)
  override val numGuestsOffset: Option[Int]            = Some(0x54)
}

object Version6ReplayOffsets extends ReplayOffsets {
  override val versionNumber: Int = 6
  override val hasPotentiallyMultipleNames: Boolean = true
  override val hasExtraData: Boolean = true

  override val magicNumberOffset: Int       = 0x00
  override val fileVersionOffset: Int       = 0x04
  override val protocolVersionOffset: Int   = 0x08
  override val spyPartyVersionOffset: Int   = 0x0C
  override val durationOffset: Int          = 0x14
  override val uuidOffset: Int              = 0x18
  override val timestampOffset: Int         = 0x28
  override val sequenceNumberOffset: Int    = 0x2C
  override val spyNameLengthOffset: Int     = 0x2E
  override val sniperNameLengthOffset: Int  = 0x2F
  override val gameResultOffset: Int        = 0x38
  override val gameTypeOffset: Int          = 0x3C
  override val levelOffset: Int             = 0x40
  override val missionsCompletedOffset: Int = 0x50
  override val playerNamesOffset: Int       = 0x64

  override val spyDisplayNameLengthOffset: Option[Int] = Some(0x30)
  override val sniperDisplayNameLength: Option[Int]    = Some(0x31)
  override val startDurationOffset: Option[Int]        = Some(0x54)
  override val numGuestsOffset: Option[Int]            = Some(0x58)
}

case class Replay(spy: String,
                  sniper: String,
                  startTime: DateTime,
                  result: GameResult,
                  level: Level,
                  loadoutType: GameType,
                  sequenceNumber: Int,
                  uuid: String,
                  version: Int,
                  startDuration: Option[Int],
                  numGuests: Option[Int],
                  missionsCompleted: Int) extends Ordered[Replay] {
  override def compare(that: Replay): Int = if (this.startTime.isBefore(that.startTime)) -1 else 1

  def isCompleted: Boolean = result != GameResultEnum.InProgress
  def spyWon: Boolean = result == GameResultEnum.CivilianShot || result == GameResultEnum.MissionWin
  def sniperWon: Boolean = result == GameResultEnum.SpyShot || result == GameResultEnum.SpyTimeout

  def winnerName: String = if (spyWon) spy else sniper
  def winnerRole: String = if (spyWon) "spy" else "sniper"

  private def formatStartTime = startDuration.map(x => f"${x / 60}%d:${x % 60}%02d")

  private def additionalGameInfo = if (formatStartTime.isDefined && numGuests.isDefined) {
    s" ${formatStartTime.get} ${numGuests.get} guests"
  } else ""

  def fullLevelName = s"${level.name} $loadoutType$additionalGameInfo"

  def description: String = s"$winnerName wins as $winnerRole on $fullLevelName"
  def smallDescription: String = s"$winnerName wins as ${winnerRole.capitalize}"
  def overtimeDescription: String = if (result == CivilianShot) {
    s"$sniper shot a civilian netting $spy $tiebreakerPoints points"
  } else {
    s"$spy earned $tiebreakerPoints points"
  }

  def tiebreakerPoints: Int = {
    if (result == CivilianShot) {
      loadoutType.x
    } else {
      missionsCompleted
    }
  }
}

object Replay {

  val HeaderDataSizeBytes = 416

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

  private def getFileVersionOffsets(data: Byte): String \/ ReplayOffsets = {
    data match {
      case 0x03 => Version3ReplayOffsets.right
      case 0x04 => Version4ReplayOffsets.right
      case 0x05 => Version5ReplayOffsets.right
      case 0x06 => Version6ReplayOffsets.right
      case _    => "Unknown replay header".left
    }
  }

  private def extractSpyNameLength(headerData: Array[Byte], offsets: ReplayOffsets) = {
    if (offsets.hasPotentiallyMultipleNames) {
      val usernameLength = headerData(offsets.spyNameLengthOffset)
      val displayNameLength = headerData(offsets.spyDisplayNameLengthOffset.get)

      if (displayNameLength != 0) {
        displayNameLength.right
      } else {
        usernameLength.right
      }
    } else {
      headerData(offsets.spyNameLengthOffset).right
    }
  }

  private def extractSniperNameLength(headerData: Array[Byte], offsets: ReplayOffsets) = {
    if (offsets.hasPotentiallyMultipleNames) {
      val usernameLength = headerData(offsets.sniperNameLengthOffset)
      val displayNameLength = headerData(offsets.sniperDisplayNameLength.get)

      if (displayNameLength != 0) {
        displayNameLength.right
      } else {
        usernameLength.right
      }
    } else {
      headerData(offsets.sniperNameLengthOffset).right
    }
  }

  private def extractGameResult(headerData: Array[Byte], offsets: ReplayOffsets): String \/ GameResult = {
    for {
      gameResult <- GameResultEnum.fromInt(headerData(offsets.gameResultOffset))
    } yield gameResult
  }

  private def extractStartTime(headerData: Array[Byte], offsets: ReplayOffsets) = {
    val timeBytes = headerData.slice(offsets.timestampOffset, offsets.timestampOffset + 4)
    val buffer = ByteBuffer.wrap(timeBytes)
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    val res = buffer.getInt() & 0xFFFFFFFFL

    new DateTime(res * 1000).right
  }

  private def extractSpyName(headerData: Array[Byte], offsets: ReplayOffsets) = {
    val spyNameLength = headerData(offsets.spyNameLengthOffset)
    new String(headerData.slice(offsets.playerNamesOffset, offsets.playerNamesOffset + spyNameLength), "UTF-8").right
  }
  private def extractSniperName(headerData: Array[Byte], offsets: ReplayOffsets) = {
    val spyNameLength = headerData(offsets.spyNameLengthOffset)
    val sniperNameLength = headerData(offsets.sniperNameLengthOffset)
    new String(headerData.slice(offsets.playerNamesOffset + spyNameLength, offsets.playerNamesOffset + spyNameLength + sniperNameLength), "UTF-8").right
  }

  private def extractLevel(headerData: Array[Byte], offsets: ReplayOffsets): String \/ Level = {
    val levelId = extractInt(headerData, offsets.levelOffset)

    val levelObj = Level.AllLevels.filter(_.checksum == levelId)

    levelObj.length match {
      case 0 => s"No level found with id $levelId".left
      case 1 => levelObj.head.right
      case _ => s"Multiple levels found with id $levelId".left
    }
  }

  private def extractSequenceNumber(headerData: Array[Byte], offsets: ReplayOffsets): String \/ Int = {
    extractShort(headerData, offsets.sequenceNumberOffset).toInt.right
  }

  private def extractUuid(headerData: Array[Byte], offsets: ReplayOffsets): String \/ String = {
   Base64
      .getEncoder
      .encodeToString(headerData.slice(offsets.uuidOffset, offsets.uuidOffset + 16)) //encode bytes to base64 string
      .split("=")(0)                                     //drop trailing = signs
      .replaceAll("\\+", "-")                            //replace + with - because that's how spyparty does things
      .replaceAll("/", "_")                              //replace / with _ ibid
      .right
  }

  private def extractNumGuests(bytes: Array[Byte], offsets: ReplayOffsets) = {
    offsets.numGuestsOffset.map(extractInt(bytes, _)).right
  }

  private def extractStartDuration(bytes: Array[Byte], offsets: ReplayOffsets) = {
    offsets.startDurationOffset.map(extractInt(bytes, _)).right
  }

  private def extractNumberMissionsCompleted(bytes: Array[Byte], offsets: ReplayOffsets) = {
    val missionCompletedBytes = extractInt(bytes, offsets.missionsCompletedOffset)
    Integer.bitCount(missionCompletedBytes).right
  }

  def fromInputStream(is: DataInputStream): String \/ Replay = {
    val headerData = new Array[Byte](HeaderDataSizeBytes)

    val bytesRead = is.read(headerData)

    if (bytesRead != HeaderDataSizeBytes) {
      return "Could not read entire replay data header".left
    }

    for {
      _                 <- verifyMagicNumber(headerData)
      dataOffsets       <- getFileVersionOffsets(headerData(4))
      gameResult        <- extractGameResult(headerData, dataOffsets)
      startTime         <- extractStartTime(headerData, dataOffsets)
      spy               <- extractSpyName(headerData, dataOffsets)
      sniper            <- extractSniperName(headerData, dataOffsets)
      gameType          <- GameType.fromInt(extractInt(headerData, dataOffsets.gameTypeOffset))
      uuid              <- extractUuid(headerData, dataOffsets)
      level             <- extractLevel(headerData, dataOffsets)
      sequence          <- extractSequenceNumber(headerData, dataOffsets)
      numGuests         <- extractNumGuests(headerData, dataOffsets)
      startDuration     <- extractStartDuration(headerData, dataOffsets)
      missionsCompleted <- extractNumberMissionsCompleted(headerData, dataOffsets)
    } yield Replay(spy, sniper, startTime, gameResult, level, gameType, sequence, uuid, dataOffsets.versionNumber, numGuests, startDuration, missionsCompleted)
  }
}
