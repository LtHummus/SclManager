package com.lthummus.sclmanager.parsing

import com.lthummus.sclmanager.parsing.BoutTypeEnum.BoutType
import com.typesafe.config.ConfigFactory

import com.lthummus.sclmanager.scaffolding.SystemConfig._


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
      val game2winner = games(1).winnerName
      val game2winnerRole = games(1).winnerRole

      (game1winner, game1winnerRole, game2winner, game2winnerRole) match {
        case (_, "spy", _, "spy") => s"Spies win ${games.head.fullLevelName}"
        case (_, "sniper", _, "sniper") => s"Snipers win ${games.head.fullLevelName}"
        case _ => s"$game1winner sweeps ${games.head.fullLevelName}"
      }
    }

  }

  def getGameSummary: List[String] = {
    if (kind.hasOvertime(player1Score, player2Score)) {
      val overtimeGames = orderedReplays.takeRight(2)
      orderedReplays.dropRight(2).grouped(2).map(getSummaryForGroup).toList ++ overtimeGames.map(it => s"**OVERTIME** ${it.description}")
    } else {
      orderedReplays.grouped(2).map(getSummaryForGroup).toList
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
  val Config = ConfigFactory.load()

  val PointsForWin = Config.getIntWithStage("tournament.pointsPerWin")
  val PointsForDraw = Config.getIntWithStage("tournament.pointsPerDraw")
  val PointsForLoss = Config.getIntWithStage("tournament.pointsPerLoss")
}
