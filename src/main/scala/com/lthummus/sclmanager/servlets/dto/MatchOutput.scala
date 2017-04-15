package com.lthummus.sclmanager.servlets.dto

import com.lthummus.sclmanager.parsing._
import org.joda.time.DateTime
import zzz.generated.tables.records.{BoutRecord, DraftRecord, GameRecord, PlayerRecord}

import scalaz._
import Scalaz._

case class Match(id: Int,
                 week: Int,
                 league: String,
                 player1: SimplePlayer,
                 player2: SimplePlayer,
                 status: Int,
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
                sequence: Int) {
  def asReplay: Replay = {
    val disjointReplay = for {
      parsedResult <- GameResult.fromString(result)
      parsedLevel <- Level.getLevelByName(level)
      parsedGameType <- GameType.fromString(gameType)
    } yield Replay(spy, sniper, new DateTime(id * 1000L), parsedResult, parsedLevel, parsedGameType, sequence)

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
  def fromDatabaseRecordWithGames(record: BoutRecord, games: List[GameRecord], playerMap: Map[String, PlayerRecord], draft: Option[Draft]): Match = {
    val gameList = games.map(Game.fromDatabaseRecord(_, playerMap))
    val packagedMatchUrl = Option(record.getMatchUrl)

    val maybeBout = gameList.size match {
      case 0 => None
      case _ => Some(Bout(gameList.map(_.asReplay)))
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
        |Game link: [url]${record.getMatchUrl}[/url]
      """.stripMargin)

    val summary = maybeBout.map(bout =>
      s"""
        |Results for ${bout.player1} v. ${bout.player2}
        |
        |$draftSummary
        |
        |${bout.getGameSummary.mkString("\n")}
      """.stripMargin)



    Match(record.getId,
      record.getWeek,
      record.getDivision,
      SimplePlayer.fromDatabaseRecord(playerMap(record.getPlayer1)),
      SimplePlayer.fromDatabaseRecord(playerMap(record.getPlayer2)),
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
      record.getGametype,
      record.getSequence)
  }
}