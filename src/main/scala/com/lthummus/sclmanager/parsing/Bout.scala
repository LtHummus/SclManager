package com.lthummus.sclmanager.parsing


case class Bout(replays: Iterable[Replay]) {
  val orderedReplays = replays.filter(_.isCompleted).toList.sorted

  val player1 = orderedReplays.head.spy
  val player2 = orderedReplays.head.sniper

  val player1Score = orderedReplays.count(_.winnerName == player1)
  val player2Score = orderedReplays.count(_.winnerName == player2)

  def pointsForPlayer(playerName: String): Int = {
    val ourScore = if (player1 == playerName) player1Score else player2Score
    val theirScore = if (player1 == playerName) player2Score else player1Score

    (ourScore, theirScore) match {
      //TODO: read this from config
      case (x, y) if x == y => 1
      case (x, y) if x > y => 2
      case _ => 0
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

  def getGameSummary = orderedReplays.grouped(2).map(getSummaryForGroup).toList

  def getScoreLine = {
    if (player1Score > player2Score) {
      s"$player1 wins $player1Score-$player2Score"
    } else if (player2Score > player1Score) {
      s"$player2 wins $player2Score-$player1Score"
    } else {
      s"TieParty $player1Score-$player2Score"
    }
  }

}
