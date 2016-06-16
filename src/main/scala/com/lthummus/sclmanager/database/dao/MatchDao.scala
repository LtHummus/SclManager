package com.lthummus.sclmanager.database.dao

import com.lthummus.sclmanager.database.data.Match
import org.jooq.DSLContext
import zzz.generated.Tables

import scala.collection.JavaConversions._


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
}
