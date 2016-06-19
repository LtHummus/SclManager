package com.lthummus.sclmanager.database.dao

import org.jooq.DSLContext
import zzz.generated.Tables
import zzz.generated.tables.records.PlayerRecord

import scala.collection.JavaConversions._

object PlayerDao {

  def all()(implicit dslContext: DSLContext) = {
    dslContext.selectFrom(Tables.PLAYER).fetch().toList
  }

  def getByPlayerId(id: Int)(implicit dslContext: DSLContext): Option[PlayerRecord] = {
    val res = dslContext.selectFrom(Tables.PLAYER).where(Tables.PLAYER.ID.eq(id)).fetch()

    res.size() match {
      case 0 => None
      case _ => Some(res.get(0))
    }
  }

  def getByLeagueId(id: Int)(implicit dslContext: DSLContext): List[PlayerRecord] = {
    dslContext
      .selectFrom(Tables.PLAYER)
      .where(Tables.PLAYER.LEAGUE.eq(id))
      .fetch()
      .toList
  }

  def getPlayerByName(name: String)(implicit dslContext: DSLContext): Option[PlayerRecord] = {
    val res = dslContext
      .selectFrom(Tables.PLAYER)
      .where(Tables.PLAYER.NAME.eq(name.toLowerCase))
      .fetch()
      .toList

    res.size match {
      case 0 => None
      case _ => Some(res.get(0))
    }
  }

  def getNumberOfMatchesPlayed(id: Int)(implicit dslContext: DSLContext): Int = {
    dslContext
      .selectCount()
      .from(Tables.MATCH)
      .where(Tables.MATCH.PLAYER1.eq(id))
      .or(Tables.MATCH.PLAYER2.eq(id)).fetchOne(0, classOf[Int])
  }

}
