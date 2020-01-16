package com.lthummus.sclmanager.database.dao

import com.lthummus.sclmanager.servlets.dto.{DraftInput, DraftPayload}
import org.jooq.DSLContext
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._
import zzz.generated.Tables
import zzz.generated.tables.records.DraftRecord

import scala.collection.JavaConversions._

object DraftDao {

  implicit val formats = Serialization.formats(NoTypeHints)

  def all()(implicit dslContext: DSLContext): List[DraftRecord] = {
    dslContext.selectFrom(Tables.DRAFT).fetch().toList
  }

  private def translateSinglePlayerName(x: String)(implicit dslContext: DSLContext): String = {
    if (x.endsWith("/steam") && PlayerDao.getByPlayerName(x.dropRight(6)).isDefined) {
      x.dropRight(6)
    } else if (x.endsWith("steam")) {
      PlayerDao.getPlayerFromReplayName(x).getOrElse(x)
    } else {
      x
    }
  }

  private def buildTranslationMap(player1: String, player2: String)(implicit dslContext: DSLContext) = {
    val realPlayer1Name = translateSinglePlayerName(player1)
    val realPlayer2Name = translateSinglePlayerName(player2)

    Map(player1 -> realPlayer1Name, player2 -> realPlayer2Name)
  }

  private def buildPlayerList(player1: String, player2: String, translations: Map[String, String]) = {
    Seq(translations(player1), translations(player2)).sorted
  }

  private def patchPlayload(payload: DraftPayload, translations: Map[String, String]) = {
    if (translations.forall{ case (a, b) => a == b}) {
      payload //no patching necessary
    } else {
      val newFirstSpyName = translations(payload.firstSpy.toLowerCase)
      val newStartPlayerName = translations(payload.startPlayer.toLowerCase)
      val newBannedMaps = payload.bannedMaps.map(x => x.copy(picker = translations(x.picker.toLowerCase)))
      val newPickedMaps = payload.pickedMaps.map(x => x.copy(picker = translations(x.picker.toLowerCase)))
      val newRestrictedMaps = payload.restrictedMaps.map(x => x.copy(picker = translations(x.picker.toLowerCase)))

      DraftPayload(newBannedMaps, newPickedMaps, newRestrictedMaps, newStartPlayerName, newFirstSpyName)
    }
  }

  def persist(input: DraftInput)(implicit dslContext: DSLContext) = {
    val translationMap = buildTranslationMap(input.player1, input.player2)
    val players = buildPlayerList(input.player1, input.player2, translationMap)
    val record = dslContext.newRecord(Tables.DRAFT)

    record.setPlayer1(players.head.toLowerCase)
    record.setPlayer2(players(1).toLowerCase)
    record.setRoomCode(input.roomCode)
    record.setPayload(write(patchPlayload(input.payload, translationMap)))

    record.insert()


  }

  def getById(id: Int)(implicit dslContext: DSLContext): Option[DraftRecord] = {
    val res = dslContext.selectFrom(Tables.DRAFT).where(Tables.DRAFT.ID.eq(id)).fetch

    res.size() match {
      case 0 => None
      case 1 => Some(res(0))
      case _ => throw new Exception(s"More than one draft for id $id")
    }
  }

  def getLatestForPlayers(players: Seq[String])(implicit dslContext: DSLContext): Option[DraftRecord] = {
    players.ensuring(players.length == 2)

    val sortedPlayers = players.sorted

    Option(dslContext.selectFrom(Tables.DRAFT)
      .where(Tables.DRAFT.PLAYER1.eq(sortedPlayers.head))
      .and(Tables.DRAFT.PLAYER2.eq(sortedPlayers(1)))
      .orderBy(Tables.DRAFT.TIME.desc())
      .limit(1)
      .fetchOne())


  }

  def isDraftUsed(id: Int)(implicit dslContext: DSLContext): Boolean =
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
