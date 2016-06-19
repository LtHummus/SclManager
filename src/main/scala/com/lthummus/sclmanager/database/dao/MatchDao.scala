package com.lthummus.sclmanager.database.dao

import com.lthummus.sclmanager.parsing.Bout
import org.jooq.DSLContext
import zzz.generated.Tables
import zzz.generated.tables.records.{MatchRecord, PlayerRecord}

import scala.collection.JavaConversions._
import scalaz._
import Scalaz._

object MatchDao {
  def getById(id: Int)(implicit dslContext: DSLContext): Option[MatchRecord] = {
    val res = dslContext.selectFrom(Tables.MATCH).where(Tables.MATCH.ID.eq(id)).fetch()

    res.size() match {
      case 0 => None
      case _ => Some(res(0))
    }
  }

  def getOutstandingMatchesFromLeague(id: Int)(implicit dslContext: DSLContext): List[MatchRecord] = {
    dslContext.selectFrom(Tables.MATCH).where(Tables.MATCH.STATUS.eq(0)).toList
  }

  def getNextToBePlayedByPlayers(player1: Int, player2: Int)(implicit dslContext: DSLContext) = {
    val res1 = dslContext
      .selectFrom(Tables.MATCH)
      .where(Tables.MATCH.PLAYER1.eq(player1))
      .and(Tables.MATCH.PLAYER2.eq(player2))
      .and(Tables.MATCH.STATUS.eq(0))
      .orderBy(Tables.MATCH.WEEK)
      .fetch()

    val res2 = dslContext
      .selectFrom(Tables.MATCH)
      .where(Tables.MATCH.PLAYER1.eq(player2))
      .and(Tables.MATCH.PLAYER2.eq(player1))
      .and(Tables.MATCH.STATUS.eq(0))
      .orderBy(Tables.MATCH.WEEK)
      .fetch()

    val matchesFound = res1.toList ++ res2.toList

    matchesFound.size match {
      case 0 => None
      case _ => Some(matchesFound.sortWith(_.getWeek < _.getWeek).head)
    }
  }

  private def buildNameDecoder(player1: PlayerRecord, player2: PlayerRecord): PartialFunction[Int, String] = {
    new PartialFunction[Int, String] {
      def apply(id: Int) = {
        if (id == player1.getId)
          player1.getName
        else
          player2.getName
      }

      def isDefinedAt(id: Int) = id == player1.getId || id == player2.getId
    }
  }

  def getBoutData(matchId: Int)(implicit dslContext: DSLContext): String \/ Bout = {
    for {
      matchData <- getById(matchId).toRightDisjunction(s"No match found with id $matchId")
      player1 <- PlayerDao.getByPlayerId(matchData.getPlayer1).toRightDisjunction(s"No player found with id ${matchData.getPlayer1}")
      player2 <- PlayerDao.getByPlayerId(matchData.getPlayer2).toRightDisjunction(s"No player found with id ${matchData.getPlayer2}")
      gameList <- GameDao.getGamesByMatchId(matchId, buildNameDecoder(player1, player2))
    } yield Bout(gameList)
  }
}
