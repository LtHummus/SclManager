package com.lthummus.sclmanager.database.dao

import com.lthummus.sclmanager.parsing.{GameResult, GameType, Level, Replay}
import org.joda.time.DateTime
import org.jooq.DSLContext
import zzz.generated.Tables
import zzz.generated.tables.records.GameRecord

import scala.collection.JavaConverters._
import scalaz._
import Scalaz._

object GameDao {

  implicit class ConvertableToReplay(record: GameRecord) {
    def asReplay: String \/ Replay = {
      for {
        resultValue <- GameResult.fromInt(record.getResult)
        level <- Level.getLevelByName(record.getVenue)
        decodedGameType <- GameType.fromString(record.getGametype)
      } yield Replay(record.getSpy, record.getSniper, new DateTime(), resultValue, level, decodedGameType)
    }
  }

  implicit class ConvertableFromReplay(replay: Replay) {
    def toDatabase(matchId: Int) = {
      new GameRecord(null, replay.spy, replay.sniper, matchId, GameResult.toInt(replay.result), 2, replay.level.name, replay.loadoutType.toString)
    }
  }

  def all(implicit dslContext: DSLContext) = {
    dslContext
      .selectFrom(Tables.GAME)
      .fetch().asScala.toList
  }

  def getGameRecordsByBoutId(matchId: Int)(implicit dslContext: DSLContext): List[GameRecord] = {
    dslContext
      .selectFrom(Tables.GAME)
      .where(Tables.GAME.BOUT.eq(matchId))
      .orderBy(Tables.GAME.SEQUENCE)
      .fetch().asScala.toList
  }

  def getGamesByBoutId(boutId: Int)(implicit dslContext: DSLContext): String \/ List[Replay] = {
    val res = dslContext
      .selectFrom(Tables.GAME)
      .where(Tables.GAME.BOUT.eq(boutId))
      .orderBy(Tables.GAME.SEQUENCE)
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
      case e: Throwable => e.getMessage.left
    }
  }
}
