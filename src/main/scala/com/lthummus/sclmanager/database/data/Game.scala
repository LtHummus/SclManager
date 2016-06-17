package com.lthummus.sclmanager.database.data

import com.lthummus.sclmanager.parsing.{GameResult, GameType, Level, Replay}
import zzz.generated.tables.records.GameRecord

import scalaz._
import Scalaz._


object Game {
  def toReplay(record: GameRecord, spyName: String, sniperName: String): String \/ Replay = {
    for {
      resultValue <- GameResult.fromInt(record.getResult)
      level <- Level.getLevelByName(record.getLevel)
      decodedGameType <- GameType.fromString(record.getGametype)
    } yield Replay(spyName, sniperName, record.getSequence, resultValue, level, decodedGameType)
  }

  def toDb(replay: Replay, matchId: Int, idResolver: PartialFunction[String, Int]) = {
    new GameRecord(null, idResolver(replay.spy), idResolver(replay.sniper), matchId, GameResult.toInt(replay.result),replay.sequence, replay.level.name, replay.loadoutType.toString)
  }
}
