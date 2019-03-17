package com.lthummus.sclmanager.parsing


object BoutTypeEnum {



  sealed abstract class BoutType(name: String, internalId: Int) {
    def isComplete(player1Score: Int, player2Score: Int): Boolean
    def hasOvertime(player1Score: Int, player2Score: Int): Boolean

    protected def sortedScores(score1: Int, score2: Int): Seq[Int] = Seq(score1, score2).sorted


    override def toString: String = name
  }

  def fromInt(x: Int): BoutType = {
    x match {
      case 0 => Standard
      case 1 => Promotion
      case 2 => Relegation
      case 3 => LeagueChampionship
      case 4 => Shortened
      case _ => throw new Exception(s"Unknown BoutType $x")
    }
  }

  case object Standard extends BoutType("Standard", 0) {
    override def isComplete(player1Score: Int, player2Score: Int): Boolean = {
      sortedScores(player1Score, player2Score) match {
        case _ :: winner :: _ if winner == 7                   => true
        case loser :: winner :: _ if winner == 6 && loser == 6 => true
        case _ => false
      }
    }

    override def hasOvertime(player1Score: Int, player2Score: Int): Boolean = false
  }

  case object Promotion extends BoutType("Promotion", 1) {
    override def isComplete(player1Score: Int, player2Score: Int): Boolean = {
      sortedScores(player1Score, player2Score) match {
        case _ :: winner :: _ if winner == 9                   => true
        case loser :: winner :: _ if loser == 8 && winner == 8 => true
        case _                                                 => false
      }
    }

    override def hasOvertime(player1Score: Int, player2Score: Int): Boolean = {
      val ourSortedScores = sortedScores(player1Score, player2Score)
      val winnerScore = ourSortedScores.head
      val loserScore = ourSortedScores(1)

      winnerScore == loserScore
    }
  }

  case object Relegation extends BoutType("Relegation", 2) {
    override def isComplete(player1Score: Int, player2Score: Int): Boolean = {
      sortedScores(player1Score, player2Score) match {
        case _ :: winner :: _ if winner == 9                   => true
        case loser :: winner :: _ if loser == 8 && winner == 8 => true
        case _                                                 => false
      }
    }

    override def hasOvertime(player1Score: Int, player2Score: Int): Boolean = {
      val ourSortedScores = sortedScores(player1Score, player2Score)
      val winnerScore = ourSortedScores.head
      val loserScore = ourSortedScores(1)

      winnerScore == loserScore
    }
  }

  case object LeagueChampionship extends BoutType("League Championship", 3) {
    override def isComplete(player1Score: Int, player2Score: Int): Boolean = true

    override def hasOvertime(player1Score: Int, player2Score: Int): Boolean = false
  }

  case object Shortened extends BoutType("Shortened", 4) {
    //this exists because one match, a player surrendered down 6-0
    override def isComplete(player1Score: Int, player2Score: Int): Boolean = {
      sortedScores(player1Score, player2Score) match {
        case _ :: winner :: _ if winner == 6                   => true
        case loser :: winner :: _ if winner == 5 && loser == 5 => true
        case _ => false
      }
    }

    override def hasOvertime(player1Score: Int, player2Score: Int): Boolean = false
  }

}
