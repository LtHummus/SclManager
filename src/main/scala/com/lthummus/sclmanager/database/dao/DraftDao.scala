package com.lthummus.sclmanager.database.dao

import com.lthummus.sclmanager.servlets.dto.DraftInput
import org.jooq.DSLContext
import zzz.generated.Tables
import zzz.generated.tables.records.DraftRecord

import scala.collection.JavaConversions._

object DraftDao {

  def all()(implicit dslContext: DSLContext) = {
    dslContext.selectFrom(Tables.DRAFT).fetch().toList
  }

  def persist(input: DraftInput)(implicit dslContext: DSLContext) = {
    val players = Seq(input.player1, input.player2).sorted
    val record = dslContext.newRecord(Tables.DRAFT)

    record.setPlayer1(players.head)
    record.setPlayer2(players(1))
    record.setRoomCode(input.roomCode)
    record.setPayload(input.payload)

    record.insert()
  }

  def getById(id: Int)(implicit dslContext: DSLContext) = {
    val res = dslContext.selectFrom(Tables.DRAFT).where(Tables.DRAFT.ID.eq(id)).fetch

    res.size() match {
      case 0 => None
      case 1 => Some(res.get(0))
      case _ => ???
    }
  }

  def getLatestForPlayers(players: Seq[String])(implicit dslContext: DSLContext) = {
    players.ensuring(players.length == 2)

    val sortedPlayers = players.sorted

    val res = dslContext.selectFrom(Tables.DRAFT)
      .where(Tables.DRAFT.PLAYER1.eq(sortedPlayers.head))
      .and(Tables.DRAFT.PLAYER2.eq(sortedPlayers(1)))
      .orderBy(Tables.DRAFT.TIME.desc()).fetch()

    res.size() match {
      case 0 => None
      case 1 => Some(res.get(0))
      case _ => ???
    }
  }

  def isDraftUsed(id: Int)(implicit dslContext: DSLContext) =
    dslContext.selectFrom(Tables.BOUT).where(Tables.BOUT.DRAFT.eq(id)).fetch().size() != 0


  private def killUsedDraft(draft: DraftRecord)(implicit dslContext: DSLContext) = {
    if (isDraftUsed(draft.getId))
      None
    else
      Some(draft)
  }

  def getLatestUnusedDraftForPlayer(players: Seq[String])(implicit dslContext: DSLContext): Option[DraftRecord] = {
    getLatestForPlayers(players).flatMap(killUsedDraft)
  }
}
