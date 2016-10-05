package com.lthummus.sclmanager.servlets.dto

import com.lthummus.sclmanager.parsing._
import org.joda.time.DateTime
import zzz.generated.tables.records.{BoutRecord, DraftRecord, GameRecord, PlayerRecord}

import scalaz._
import Scalaz._

case class Match(id: Int,
                 week: Int,
                 league: String,
                 player1: Player,
                 player2: Player,
                 status: Int,
                 winner: Option[Player],
                 games: Option[List[Game]],
                 matchUrl: Option[String],
                 draft: Option[Draft],
                 summary: Option[String]) {

}

case class Game(id: Int,
                spy: String,
                sniper: String,
                matchId: Int,
                result: String,
                level: String,
                gameType: String) {
  def asReplay: Replay = {
    val disjointReplay = for {
      parsedResult <- GameResult.fromString(result)
      parsedLevel <- Level.getLevelByName(level)
      parsedGameType <- GameType.fromString(gameType)
    } yield Replay(spy, sniper, new DateTime(id * 1000L), parsedResult, parsedLevel, parsedGameType)

    disjointReplay match {
      case -\/(error) => ???
      case \/-(replay) => replay
    }
  }
}

case class MatchList(matches: Seq[Match])

object Match {
  def fromDatabaseRecordWithGames(record: BoutRecord, games: Option[List[GameRecord]], playerMap: Map[String, PlayerRecord], draft: Option[Draft]) = {
    val gameList = games.map(x => x.map(Game.fromDatabaseRecord(_, playerMap)))
    val winner = if (record.getWinner == null) None else Some(Player.fromDatabaseRecord(playerMap(record.getWinner)))
    val packagedMatchUrl = Option(record.getMatchUrl)

    val summary = for {
      boutSummary <- gameList.map(_.map(_.asReplay)).map(Bout(_))
      draftSummary <- draft.map(_.asForumPost)
    } yield {
      s"""
        |Results for ${boutSummary.player1} v. ${boutSummary.player2}
        |
        |$draftSummary
        |
        |${boutSummary.getForumPost}
      """.stripMargin
    }


    Match(record.getId,
      record.getWeek,
      record.getDivision,
      Player.fromDatabaseRecord(playerMap(record.getPlayer1)),
      Player.fromDatabaseRecord(playerMap(record.getPlayer2)),
      record.getStatus,
      winner,
      gameList,
      packagedMatchUrl,
      draft,
      summary)
  }
}

object Game {
  def fromDatabaseRecord(record: GameRecord, playerMap: Map[String, PlayerRecord]) = {
    val gameResult = GameResult.fromInt(record.getResult) match {
      case -\/(error) => error
      case \/-(res) => res.toString
    }
    Game(record.getId,
      record.getSpy,
      record.getSniper,
      record.getBout,
      gameResult,
      record.getVenue,
      record.getGametype)
  }
}