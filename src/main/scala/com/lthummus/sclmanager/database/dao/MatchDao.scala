package com.lthummus.sclmanager.database.dao

import com.lthummus.sclmanager.database.data.{Match, Player}
import com.lthummus.sclmanager.parsing.Bout
import org.jooq.DSLContext
import zzz.generated.Tables

import scala.collection.JavaConversions._
import scalaz._
import Scalaz._

object MatchDao {
  def getById(id: Int)(implicit dslContext: DSLContext): Option[Match] = {
    val res = dslContext.selectFrom(Tables.MATCH).where(Tables.MATCH.ID.eq(id)).fetch()

    res.size() match {
      case 0 => None
      case _ => Some(Match.fromDatabaseObject(res.get(0)))
    }
  }

  def getOutstandingMatchesFromLeague(id: Int)(implicit dslContext: DSLContext) = {
    dslContext.selectFrom(Tables.MATCH).where(Tables.MATCH.STATUS.eq(0)).toList.map(Match.fromDatabaseObject)
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
      case _ => Some(matchesFound.map(Match.fromDatabaseObject).sorted.head)
    }
  }

  private def buildNameDecoder(player1: Player, player2: Player) = {
    new PartialFunction[Int, String] {
      def apply(id: Int) = {
        if (id == player1.id)
          player1.name
        else
          player2.name
      }

      def isDefinedAt(id: Int) = id == player1.id || id == player2.id
    }
  }

  def getBoutData(matchId: Int)(implicit dslContext: DSLContext): String \/ Bout = {
    for {
      matchData <- getById(matchId)
      player1 <- PlayerDao.getByPlayerId(matchData.player1)
      player2 <- PlayerDao.getByPlayerId(matchData.player2)
      gameList <- GameDao.getGamesByMatchId(matchId, buildNameDecoder(player1, player2))
    } yield Bout(gameList)
  }
}
