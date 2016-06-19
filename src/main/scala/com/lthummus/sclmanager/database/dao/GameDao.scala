package com.lthummus.sclmanager.database.dao

import com.lthummus.sclmanager.parsing.{GameResult, GameType, Level, Replay}
import org.jooq.DSLContext
import zzz.generated.Tables
import zzz.generated.tables.records.GameRecord

import scala.collection.JavaConverters._
import scalaz._
import Scalaz._

object GameDao {

  implicit class ConvertableToReplay(record: GameRecord) {
    def asReplay(spy: String, sniper: String): String \/ Replay = {
      for {
        resultValue <- GameResult.fromInt(record.getResult)
        level <- Level.getLevelByName(record.getLevel)
        decodedGameType <- GameType.fromString(record.getGametype)
      } yield Replay(spy, sniper, record.getSequence, resultValue, level, decodedGameType)
    }
  }

  implicit class ConvertableFromReplay(replay: Replay) {
    def toDatabase(matchId: Int, idResolver: PartialFunction[String, Int]) = {
      new GameRecord(null, idResolver(replay.spy), idResolver(replay.sniper), matchId, GameResult.toInt(replay.result),replay.sequence, replay.level.name, replay.loadoutType.toString)
    }
  }


  def getGamesByMatchId(matchId: Int, nameDecoder: PartialFunction[Int, String])(implicit dslContext: DSLContext): String \/ List[Replay] = {
    val res = dslContext
      .selectFrom(Tables.GAME)
      .where(Tables.GAME.MATCH.eq(matchId))
      .orderBy(Tables.GAME.SEQUENCE)
      .fetch()

    val replays = res.asScala.map(it => it.asReplay(nameDecoder(it.getSpy), nameDecoder(it.getSniper))).collect{ case \/-(it) => it}.toList

    if (res.size() == replays.size)
      replays.right
    else
      "Could not decode all replays".left

  }

  def persistBatchRecords(records: List[GameRecord])(implicit dslContext: DSLContext) = {
    dslContext.batchInsert(records.asJava).execute()
  }
}
