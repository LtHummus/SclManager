package com.lthummus.sclmanager.parsing


object BoutTypeEnum {



  sealed abstract class BoutType(name: String, internalId: Int) {
    def isComplete(player1Score: Int, player2Score: Int): Boolean

    override def toString: String = name
  }

  def fromInt(x: Int): BoutType = {
    x match {
      case 0 => Standard
      case 1 => Promotion
      case 2 => Relegation
      case 3 => LeagueChampionship
      case _ => ???
    }
  }

  case object Standard extends BoutType("Standard", 0) {
    override def isComplete(player1Score: Int, player2Score: Int): Boolean = {
      Seq(player1Score, player2Score).sorted match {
        case loser :: winner :: _ if winner == 5 && loser <  4 => true
        case loser :: winner :: _ if winner == 5 && loser == 5 => true
        case loser :: winner :: _ if winner == 6 && loser == 4 => true
        case _ => false
      }
    }
  }
  case object Promotion extends BoutType("Promotion", 1) {
    override def isComplete(player1Score: Int, player2Score: Int): Boolean = {
      Seq(player1Score, player2Score).sorted match {
        case _ :: winner :: _ if winner == 9                   => true
        case loser :: winner :: _ if loser == 8 && winner == 8 => true
        case _                                                 => false
      }
    }
  }

  case object Relegation extends BoutType("Relegation", 2) {
    override def isComplete(player1Score: Int, player2Score: Int): Boolean = {
      Seq(player1Score, player2Score).sorted match {
        case _ :: winner :: _ if winner == 9                   => true
        case loser :: winner :: _ if loser == 8 && winner == 8 => true
        case _                                                 => false
      }
    }
  }

  case object LeagueChampionship extends BoutType("League Championship", 3) {
    override def isComplete(player1Score: Int, player2Score: Int): Boolean = true
  }

}
