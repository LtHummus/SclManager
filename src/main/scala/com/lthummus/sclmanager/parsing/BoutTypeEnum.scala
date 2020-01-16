package com.lthummus.sclmanager.parsing

import scala.annotation.tailrec

sealed trait BoutResult
case class CompletedBout(player1Score: Int, player2Score: Int) extends BoutResult
case class Incomplete(reason: String) extends BoutResult

object BoutTypeEnum {
  sealed abstract class BoutType(name: String, internalId: Int) {
    def handleReplays(replays: Iterable[Replay], player1: String, player2: String): BoutResult
    def summarizeGames(replays: Iterable[Replay], player1: String, player2: String): String = summarizeMainGames(replays, player1, player2)
    protected def sortedScores(score1: Int, score2: Int): Seq[Int] = Seq(score1, score2).sorted
    override def toString: String = name

    protected def summarizeMainGames(replays: Iterable[Replay], player1: String, player2: String): String = {
      @tailrec def internal(games: List[Replay], buffer: String): String = {
        if (games.isEmpty) {
          buffer
        } else {
          val currentMapConfiguration = games.head.fullLevelName

          //find all games that were played on this
          val ourGames = getGamesForLayout(games, currentMapConfiguration)
          val newBuffer = buffer + "**" +  currentMapConfiguration + "**\n" + getGameSummaryForManyGames(ourGames, player1, player2).replaceAll("<br />", "\n") + "\n\n"

          internal(games.drop(ourGames.length), newBuffer)
        }
      }

      internal(replays.toList, "")
    }

    protected def getGameSummaryForManyGames(games: List[Replay], player1: String, player2: String): String = {
      if (games.size == 1) {
        val onlyGame = games.head
        s"${onlyGame.winnerName} wins as ${onlyGame.winnerRole}"
      } else if (games.forall(_.winnerRole == "spy")) {
        s"Spy wins ${games.length} games"
      } else if (games.forall(_.winnerRole == "sniper")) {
        s"Sniper wins ${games.length} games"
      } else if (games.forall(_.winnerName == player1)) {
        s"$player1 wins ${games.length} games"
      } else if (games.forall(_.winnerName == player2)) {
        s"$player2 wins ${games.length} games"
      } else {
        games.map(x => s"${x.smallDescription}").mkString("<br />")
      }

    }

    protected def getGamesForLayout(haystack: List[Replay], layout: String): List[Replay] = {
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
  }

  def fromInt(x: Int): BoutType = {
    x match {
      case 0 => Standard
      case 1 => Playoff
      case 3 => LeagueChampionship
      case 4 => Shortened
      case _ => throw new Exception(s"Unknown BoutType $x")
    }
  }

  def fromString(x: String): BoutType = {
    x match {
      case "Standard"           => Standard
      case "Promotion"          => Playoff
      case "LeagueChampionship" => LeagueChampionship
      case "Shortened"          => Shortened
      case _                    => throw new Exception(s"Unknown BoutType $x")
    }
  }

  case object Standard extends BoutType("Standard", 0) {
    private val NumToWin = 7
    override def handleReplays(replays: Iterable[Replay], player1: String, player2: String): BoutResult = {
      val mainReplays = replays.take((NumToWin - 1) * 2)
      val player1Score: Int = mainReplays.count(_.winnerName == player1)
      val player2Score: Int = mainReplays.count(_.winnerName == player2)

      val completed = sortedScores(player1Score, player2Score) match {
        case _     :: winner :: _ if winner == NumToWin                              => true
        case loser :: winner :: _ if loser == NumToWin - 1 && winner == NumToWin - 1 => true
        case _                                                                       => false
      }

      if (!completed) {
        Incomplete("This doesn't look like a complete match. Are all games included?")
      } else {
        CompletedBout(player1Score, player2Score)
      }
    }

  }

  case object Playoff extends BoutType("Promotion", 1) {
    private val NumToWin = 9

    override def handleReplays(replays: Iterable[Replay], player1: String, player2: String): BoutResult = {
      val mainReplays = replays.take((NumToWin * 2) - 1)
      val overtimeReplays = replays.drop((NumToWin * 2) - 1)

      val player1Score: Int = mainReplays.count(_.winnerName == player1)
      val player2Score: Int = mainReplays.count(_.winnerName == player2)

      val mainWinnerResolved = sortedScores(player1Score, player2Score) match {
        case _     :: winner :: _ if winner == NumToWin                              => true
        case loser :: winner :: _ if loser == NumToWin - 1 && winner == NumToWin - 1 => true
        case _                                                                       => false
      }

      if (mainWinnerResolved) {
        CompletedBout(player1Score, player2Score)
      } else {
        val overtimeRounds = overtimeReplays.grouped(2).toSeq.flatten
        val overtimeScores = overtimeRounds.groupBy(_.spy).mapValues { replays =>
          replays.map(_.tiebreakerPoints).sum
        }

        val player1RealScore = player1Score + overtimeScores(player1)
        val player2RealScore = player2Score + overtimeScores(player2)

        if (player1RealScore == player2RealScore) {
          Incomplete("This doesn't look like a complete SCL match. Are all replays included?")
        } else {
          CompletedBout(player1RealScore, player2RealScore)
        }
      }


    }

    override def summarizeGames(replays: Iterable[Replay], player1: String, player2: String): String = {
      val mainReplays = replays.take((NumToWin * 2) - 1)
      val overtimeReplays = replays.drop((NumToWin * 2) - 1)

      val mainGameSummary = summarizeMainGames(mainReplays, player1, player2)
      val overtimeRounds = overtimeReplays.grouped(2).toSeq.flatten
      val overtimeScores = overtimeRounds.groupBy(_.spy).mapValues { replays =>
        replays.map(_.tiebreakerPoints).sum
      }

      mainGameSummary + "\n\n\n" + s"$player1 got ${overtimeScores(player1)} points in overtime. \n$player2 got ${overtimeScores(player2)} points in overtime."
    }
  }

  case object LeagueChampionship extends BoutType("League Championship", 3) {
    override def handleReplays(replays: Iterable[Replay], player1: String, player2: String): BoutResult = {
      val player1Score: Int = replays.count(_.winnerName == player1)
      val player2Score: Int = replays.count(_.winnerName == player2)

      CompletedBout(player1Score, player2Score)
    }

  }

  case object Shortened extends BoutType("Shortened", 4) {
    override def handleReplays(replays: Iterable[Replay], player1: String, player2: String): BoutResult = {
      val player1Score: Int = replays.count(_.winnerName == player1)
      val player2Score: Int = replays.count(_.winnerName == player2)

      CompletedBout(player1Score, player2Score)
    }

  }

}
