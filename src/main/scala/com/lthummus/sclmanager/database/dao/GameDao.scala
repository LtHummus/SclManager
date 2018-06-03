package com.lthummus.sclmanager.database.dao

import java.sql.Timestamp

import com.lthummus.sclmanager.parsing._
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
import org.joda.time.DateTime
import org.jooq.DSLContext
import org.jooq.exception.DataAccessException
import org.slf4j.LoggerFactory
import zzz.generated.Tables
import zzz.generated.tables.records.GameRecord

import scala.collection.JavaConverters._
import scalaz._
import Scalaz._

object GameDao {

  private val Logger = LoggerFactory.getLogger("GameDao")

  implicit class ConvertableToReplay(record: GameRecord) {
    def asReplay: String \/ Replay = {
      for {
        resultValue <- GameResultEnum.fromInt(record.getResult)
        level <- Level.getLevelByName(record.getVenue)
        decodedGameType <- GameType.fromString(record.getGametype)
      } yield Replay(record.getSpy, record.getSniper, new DateTime(), resultValue, level, decodedGameType, record.getSequence, record.getUuid, -1)
    }
  }

  implicit class ConvertableFromReplay(replay: Replay) {
    def toDatabase(matchId: Int): GameRecord = {
      new GameRecord(null, replay.spy, replay.sniper, matchId, replay.result.internalId, replay.sequenceNumber, replay.level.name, replay.loadoutType.toString, replay.uuid, new Timestamp(replay.startTime.getMillis))
    }
  }

  def all(implicit dslContext: DSLContext) = {
    dslContext
      .selectFrom(Tables.GAME)
      .fetch().asScala.toList
  }

  def deleteBelongingToMatch(matchId: Int)(implicit dslContext: DSLContext) = {
    dslContext
      .deleteFrom(Tables.GAME)
      .where(Tables.GAME.BOUT.eq(matchId))
      .execute()
  }

  def getGameRecordsByBoutId(matchId: Int)(implicit dslContext: DSLContext): List[GameRecord] = {
    dslContext
      .selectFrom(Tables.GAME)
      .where(Tables.GAME.BOUT.eq(matchId))
      .orderBy(Tables.GAME.TIMESTAMP)
      .fetch().asScala.toList
  }

  def getGamesByBoutId(boutId: Int)(implicit dslContext: DSLContext): String \/ List[Replay] = {
    val res = dslContext
      .selectFrom(Tables.GAME)
      .where(Tables.GAME.BOUT.eq(boutId))
      .orderBy(Tables.GAME.TIMESTAMP)
      .fetch()

    val replays = res.asScala.map(it => it.asReplay).collect{ case \/-(it) => it}.toList

    if (res.size() == replays.size)
      replays.right
    else
      "Could not decode all replays".left

  }

  def persistBatchRecords(records: List[GameRecord])(implicit dslContext: DSLContext): String \/ Array[Int] = {
    try {
      dslContext.batchInsert(records.asJava).execute().right
    } catch {
      case e: DataAccessException if e.getCause.getCause.isInstanceOf[MySQLIntegrityConstraintViolationException] =>
        Logger.warn("Potential duplicate records", e)
        "It looks like I already have at least one of these replays.  Are you uploading the right file?".left

      case e: Throwable =>
        Logger.warn("Could not persist batch records", e)
        e.getMessage.left
    }
  }
}
