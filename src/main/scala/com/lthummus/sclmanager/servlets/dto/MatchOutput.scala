package com.lthummus.sclmanager.servlets.dto

import com.lthummus.sclmanager.parsing.BoutTypeEnum.BoutType
import com.lthummus.sclmanager.parsing._
import org.joda.time.DateTime
import org.jooq.Record
import zzz.generated.Tables
import zzz.generated.tables.records.{BoutRecord, DraftRecord, GameRecord, PlayerRecord}

import scalaz._
import Scalaz._

case class Match(id: Int,
                 week: Int,
                 league: String,
                 player1: SimplePlayer,
                 player2: SimplePlayer,
                 status: Int,
                 matchType: String,
                 games: Option[List[Game]],
                 matchUrl: Option[String],
                 draft: Option[Draft],
                 summary: Option[String] = None,
                 forumPost: Option[String] = None) {

}

case class Game(id: Int,
                spy: String,
                sniper: String,
                matchId: Int,
                result: String,
                level: String,
                gameType: String,
                sequence: Int,
                uuid: String,
                timestamp: DateTime) {
  def asReplay: Replay = {
    val disjointReplay = for {
      parsedResult <- GameResultEnum.fromString(result)
      parsedLevel <- Level.getLevelByName(level)
      parsedGameType <- GameType.fromString(gameType)
    } yield Replay(spy, sniper, timestamp, parsedResult, parsedLevel, parsedGameType, sequence, uuid)

    disjointReplay match {
      case -\/(_) => ???
      case \/-(replay) => replay
    }
  }
}

case class SimplePlayer(name: String, country: String)

object SimplePlayer {
  def fromDatabaseRecord(record: PlayerRecord): SimplePlayer = {
    SimplePlayer(record.getName, record.getCountry)
  }
}

case class MatchList(matches: Seq[Match])

object Match {
  def fromDatabaseRecordWithGames(record: Record, games: List[GameRecord], playerMap: Map[String, PlayerRecord], draft: Option[Draft]): Match = {
    val boutRecord = record.into(Tables.BOUT)
    val gameList = games.map(Game.fromDatabaseRecord(_, playerMap))
    val packagedMatchUrl = Option(boutRecord.getMatchUrl)

    val maybeBout = gameList.size match {
      case 0 => None
      case _ => Some(Bout(gameList.map(_.asReplay), BoutTypeEnum.fromInt(boutRecord.getBoutType)))
    }

    val draftSummary = draft.map(_.asForumPost).getOrElse("")

    val forumPost = maybeBout.map(bout =>
      s"""
        |Results for ${bout.player1} v. ${bout.player2}
        |
        |$draftSummary
        |
        |[results]
        |${bout.getScoreLine}
        |
        |${bout.getGameSummary.mkString("\n")}
        |[/results]
        |
        |Game link: [url]${boutRecord.getMatchUrl}[/url]
      """.stripMargin)

    val summary = maybeBout.map(bout =>
      s"""
        |Results for ${bout.player1} v. ${bout.player2}
        |
        |$draftSummary
        |
        |${bout.getGameSummary.mkString("\n")}
      """.stripMargin)



    Match(boutRecord.getId,
      boutRecord.getWeek,
      boutRecord.getDivision,
      SimplePlayer.fromDatabaseRecord(playerMap(boutRecord.getPlayer1)),
      SimplePlayer.fromDatabaseRecord(playerMap(boutRecord.getPlayer2)),
      boutRecord.getStatus,
      BoutTypeEnum.fromInt(boutRecord.getBoutType).toString,
      Some(gameList),
      packagedMatchUrl,
      draft,
      summary,
      forumPost)
  }
}

object Game {
  def fromDatabaseRecord(record: GameRecord, playerMap: Map[String, PlayerRecord]): Game = {
    val gameResult = GameResultEnum.fromInt(record.getResult) match {
      case -\/(error) => error
      case \/-(res) => res.toString
    }

    val recordId = if (record.getId == null) new Integer(-1) else record.getId

    Game(recordId,
      record.getSpy,
      record.getSniper,
      record.getBout,
      gameResult,
      record.getVenue,
      record.getGametype,
      record.getSequence,
      record.getUuid,
      new DateTime(record.getTimestamp))
  }
}