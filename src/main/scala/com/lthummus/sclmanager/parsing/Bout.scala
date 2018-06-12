package com.lthummus.sclmanager.parsing

import com.lthummus.sclmanager.parsing.BoutTypeEnum.BoutType
import com.typesafe.config.ConfigFactory
import com.lthummus.sclmanager.scaffolding.SystemConfig._

import scala.annotation.tailrec


case class Bout(replays: List[Replay], kind: BoutType) {

  val orderedReplays: List[Replay] = replays.filter(_.isCompleted).sorted

  val player1: String = orderedReplays.head.spy
  val player2: String = orderedReplays.head.sniper

  val player1Score: Int = orderedReplays.count(_.winnerName == player1)
  val player2Score: Int = orderedReplays.count(_.winnerName == player2)

  val isTie: Boolean = player1Score == player2Score

  if (!kind.isComplete(player1Score, player2Score)) {
    throw new Exception("This doesn't look like a complete SCL match.  Are all the replays included?")
  }

  def pointsForPlayer(playerName: String): Int = {
    val ourScore = if (player1 == playerName) player1Score else player2Score
    val theirScore = if (player1 == playerName) player2Score else player1Score

    (ourScore, theirScore) match {
      case (x, y) if x == y => Bout.PointsForDraw
      case (x, y) if x > y => Bout.PointsForWin
      case _ => Bout.PointsForLoss
    }
  }

  def result(playerName: String): String = {
    val ourScore = if (player1 == playerName) player1Score else player2Score
    val theirScore = if (player1 == playerName) player2Score else player1Score

    (ourScore, theirScore) match {
      case (x, y) if x == y => "draw"
      case (x, y) if x > y => "win"
      case _ => "loss"
    }
  }


  private def getSummaryForGroup(games: List[Replay]) = {
    if (games.size == 1) {
      val onlyGame = games.head
      s"${onlyGame.winnerName} wins as ${onlyGame.winnerRole} on ${onlyGame.fullLevelName}"
    } else {
      val game1winner = games.head.winnerName
      val game1winnerRole = games.head.winnerRole
      val game2winnerRole = games(1).winnerRole

      (game1winnerRole, game2winnerRole) match {
        case ("spy", "spy")       => s"Spies win ${games.head.fullLevelName}"
        case ("sniper", "sniper") => s"Snipers win ${games.head.fullLevelName}"
        case _                    => s"$game1winner sweeps ${games.head.fullLevelName}"
      }
    }
  }

  private def getGameSummaryForManyGames(games: List[Replay]): String = {
    if (games.size == 1) {
      val onlyGame = games.head
      s"\t${onlyGame.winnerName} wins as ${onlyGame.winnerRole}"
    } else if (games.forall(_.winnerRole == "spy")) {
      s"\tSpy wins ${games.length} games"
    } else if (games.forall(_.winnerRole == "sniper")) {
      s"\tSniper wins ${games.length} games"
    } else if (games.forall(_.winnerName == player1)) {
      s"\t$player1 wins ${games.length} games"
    } else if (games.forall(_.winnerName == player2)) {
      s"\t$player2 wins ${games.length} games"
    } else {
      games.map(x => s"\t${x.smallDescription}").mkString("<br />")
    }

  }

  private def getGamesForLayout(haystack: List[Replay], layout: String): List[Replay] = {
    @tailrec def internal(games: List[Replay], buffer: List[Replay]): List[Replay] = {
      if (games.isEmpty) {
        buffer
      } else if (games.head.fullLevelName == layout) {
        internal(games.tail, buffer :+ games.head)
      } else {
        buffer
      }
    }

    internal(haystack, List())
  }

  def getForumGameSummary: String = {
    @tailrec def internal(games: List[Replay], buffer: String): String = {
      if (games.isEmpty) {
        buffer
      } else {
        val currentMapConfiguration = games.head.fullLevelName

        //find all games that were played on this
        val ourGames = getGamesForLayout(games, currentMapConfiguration)
        val newBuffer = buffer + currentMapConfiguration + "<br />" + getGameSummaryForManyGames(ourGames) + "<br /><br />"

        internal(games.drop(ourGames.length), newBuffer)
      }
    }

    internal(orderedReplays, "")
  }

  def getGameSummary: List[String] = {
    if (kind.hasOvertime(player1Score, player2Score)) {
      val overtimeGames = orderedReplays.takeRight(2)
      orderedReplays.dropRight(2).map(_.description) ++ overtimeGames.map(it => s"**OVERTIME** ${it.description}")
    } else {
      orderedReplays.map(_.description)
    }

  }

  def getScoreLine: String = {
    if (player1Score > player2Score) {
      s"$player1 wins $player1Score-$player2Score"
    } else if (player2Score > player1Score) {
      s"$player2 wins $player2Score-$player1Score"
    } else {
      s"TieParty $player1Score-$player2Score"
    }
  }
}

object Bout {
  private val Config = ConfigFactory.load()

  val PointsForWin: Int = Config.getIntWithStage("tournament.pointsPerWin")
  val PointsForDraw: Int = Config.getIntWithStage("tournament.pointsPerDraw")
  val PointsForLoss: Int = Config.getIntWithStage("tournament.pointsPerLoss")
}
