package com.lthummus.sclmanager.parsing


case class Match(replays: Iterable[Replay]) {
  val orderedReplays = replays.filter(_.isCompleted).toList.sorted

  val player1 = orderedReplays.head.spy
  val player2 = orderedReplays.head.sniper

  val player1Score = orderedReplays.count(_.winnerName == player1)
  val player2Score = orderedReplays.count(_.winnerName == player2)

  val groups = orderedReplays.grouped(2)

  println(s"Results for $player1 vs $player2")
  println()

  for (levelRounds <- groups) {
    if (levelRounds.size == 1) {
      val onlyGame = levelRounds.head
      println(s"${onlyGame.winnerName} wins as ${onlyGame.winnerRole} on ${onlyGame.fullLevelName}")
    } else if (levelRounds(0).winnerName == levelRounds(1).winnerName) {
      println(s"${levelRounds(0).winnerName} sweeps ${levelRounds(0).fullLevelName}")
    } else if (levelRounds(0).spy == levelRounds(0).winnerName) {
      println(s"Spies win ${levelRounds(0).fullLevelName}")
    } else {
      println(s"Snipers win ${levelRounds(0).fullLevelName}")
    }
  }

  println()

  if (player1Score > player2Score) {
    println(s"$player1 wins $player1Score-$player2Score")
  } else if (player2Score > player1Score) {
    println(s"$player2 wins $player2Score-$player1Score")
  } else {
    println("TieParty 4-4")
  }

}
