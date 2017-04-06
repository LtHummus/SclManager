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
                 games: Option[List[Game]],
                 matchUrl: Option[String],
                 draft: Option[Draft],
                 summary: String = "",
                 forumPost: String = "") {

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
      case -\/(_) => ???
      case \/-(replay) => replay
    }
  }
}

case class MatchList(matches: Seq[Match])

object Match {
  def fromDatabaseRecordWithGames(record: BoutRecord, games: List[GameRecord], playerMap: Map[String, PlayerRecord], draft: Option[Draft]): Match = {
    val gameList = games.map(Game.fromDatabaseRecord(_, playerMap))
    val packagedMatchUrl = Option(record.getMatchUrl)

    val maybeBout = gameList.size match {
      case 0 => None
      case _ => Some(Bout(gameList.map(_.asReplay)))
    }
    val p1Text = maybeBout.map(_.player1).getOrElse("Player 1")
    val p2Text = maybeBout.map(_.player2).getOrElse("Player 2")
    val summaryText = maybeBout.map(_.getGameSummary).getOrElse(List()).mkString("\n")
    val draftSummary = draft.map(_.asForumPost).getOrElse("")

    val forumPost =
      s"""
        |Results for $p1Text v. $p2Text
        |
        |$draftSummary
        |
        |[results]
        |${maybeBout.map(_.getScoreLine).getOrElse("")}
        |
        |$summaryText
        |[/results]
        |
        |Game link: [url]${record.getMatchUrl}[/url]
      """.stripMargin

    val summary =
      s"""
        |Results for $p1Text v. $p2Text
        |
        |$draftSummary
        |
        |$summaryText
      """.stripMargin



    Match(record.getId,
      record.getWeek,
      record.getDivision,
      Player.fromDatabaseRecord(playerMap(record.getPlayer1)),
      Player.fromDatabaseRecord(playerMap(record.getPlayer2)),
      record.getStatus,
      Some(gameList),
      packagedMatchUrl,
      draft,
      summary,
      forumPost)
  }
}

object Game {
  def fromDatabaseRecord(record: GameRecord, playerMap: Map[String, PlayerRecord]): Game = {
    val gameResult = GameResult.fromInt(record.getResult) match {
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
      record.getGametype)
  }
}