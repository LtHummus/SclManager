package com.lthummus.sclmanager.database.data

import com.lthummus.sclmanager.parsing.{GameResult, GameType, Level, Replay}
import zzz.generated.tables.records.GameRecord

import scalaz._
import Scalaz._
//
//case class Game(spy: Int, sniper: Int, matchId: Int, result: Int, sequence: Int, level: String, gameType: String) {
//
//}

object Game {
  def toReplay(record: GameRecord, spyName: String, sniperName: String): String \/ Replay = {
    for {
      resultValue <- GameResult.fromInt(record.result)
      level <- Level.getLevelByName(record.level)
      decodedGameType <- GameType.fromString(record.gameType)
    } yield Replay(spyName, sniperName, record.sequence, resultValue, level, decodedGameType)
  }
}
