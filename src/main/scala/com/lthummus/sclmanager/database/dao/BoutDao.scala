package com.lthummus.sclmanager.database.dao

import com.lthummus.sclmanager.parsing.{Bout, BoutTypeEnum}
import com.lthummus.sclmanager.servlets.dto.{Draft, Player}
import org.jooq.{DSLContext, Record}
import zzz.generated.Tables
import zzz.generated.tables.records._

import scala.collection.JavaConversions._
import scalaz._
import Scalaz._


case class FullBoutRecord(bout: Record, games: List[GameRecord], playerMap: Map[String, Player], draft: Option[Draft])

object BoutDao {

  def getAll()(implicit dslContext: DSLContext): List[BoutRecord] = dslContext.selectFrom(Tables.BOUT).fetch().toList

  def getNormalizedMatchesByDivision(division: String)(implicit dslContext: DSLContext): List[(String, String)] = {
    val rawBoutRecords = dslContext
      .selectFrom(Tables.BOUT)
      .where(Tables.BOUT.DIVISION.eq(division))
      .toList

    rawBoutRecords.map(record => {
      val players = Seq(record.getPlayer1, record.getPlayer2).sorted

      (players(0), players(1))
    })
  }

  def getLastUploaded()(implicit dslContext: DSLContext): BoutRecord = {
    dslContext
      .selectFrom(Tables.BOUT)
      .where(Tables.BOUT.STATUS.eq(1)).and(Tables.BOUT.DIVISION.ne("Challenger"))
      .orderBy(Tables.BOUT.TIMESTAMP.desc())
      .limit(1)
      .fetchOne()
  }

  def getById(id: Int)(implicit dslContext: DSLContext): Option[BoutRecord] = {
    Option(dslContext
      .selectFrom(Tables.BOUT)
      .where(Tables.BOUT.ID.eq(id))
      .fetchOne())
  }

  def getByIdWithDraft(id: Int)(implicit dslContext: DSLContext): Option[Record] = {
    Option(dslContext
      .selectFrom(Tables.BOUT
          .join(Tables.DIVISION).on(Tables.DIVISION.NAME.eq(Tables.BOUT.DIVISION))
          .leftJoin(Tables.DRAFT).on(Tables.BOUT.DRAFT.eq(Tables.DRAFT.ID)))
      .where(Tables.BOUT.ID.eq(id))
      .fetchOne())
  }

  def getByWeek(week: Int)(implicit dslContext: DSLContext): List[Record] = {
    dslContext
      .selectFrom(Tables.BOUT.join(Tables.DIVISION).on(Tables.BOUT.DIVISION.eq(Tables.DIVISION.NAME)))
      .where(Tables.BOUT.WEEK.eq(week))
      .toList
  }

  def getOutstandingBoutFromLeague(id: Int)(implicit dslContext: DSLContext): List[BoutRecord] = {
    dslContext.selectFrom(Tables.BOUT).where(Tables.BOUT.STATUS.eq(0)).toList
  }

  def getMatchesForPlayer(name: String)(implicit dslContext: DSLContext): List[BoutRecord] = {
    dslContext
      .selectFrom(Tables.BOUT)
      .where(Tables.BOUT.PLAYER1.eq(name))
      .or(Tables.BOUT.PLAYER2.eq(name))
      .orderBy(Tables.BOUT.WEEK)
      .fetch()
      .toList
  }


  def getNextToBePlayedByPlayers(player1: String, player2: String)(implicit dslContext: DSLContext): Option[BoutRecord] = {
    val res1 = dslContext
      .selectFrom(Tables.BOUT)
      .where(Tables.BOUT.PLAYER1.eq(player1))
      .and(Tables.BOUT.PLAYER2.eq(player2))
      .and(Tables.BOUT.STATUS.eq(0))
      .orderBy(Tables.BOUT.WEEK)
      .fetch()

    val res2 = dslContext
      .selectFrom(Tables.BOUT)
      .where(Tables.BOUT.PLAYER1.eq(player2))
      .and(Tables.BOUT.PLAYER2.eq(player1))
      .and(Tables.BOUT.STATUS.eq(0))
      .orderBy(Tables.BOUT.WEEK)
      .fetch()

    val matchesFound = res1.toList ++ res2.toList

    matchesFound.size match {
      case 0 => None
      case _ => Some(matchesFound.sortWith(_.getWeek < _.getWeek).head)
    }
  }

  def getFullBoutRecords(boutId: Int)(implicit dslContext: DSLContext): String \/ FullBoutRecord = {
    for {
      matchRes <- getByIdWithDraft(boutId) \/> s"No match found with id $boutId"
      matchData = matchRes.into(Tables.BOUT)
      player1 <- PlayerDao.getByPlayerName(matchData.getPlayer1) \/> s"No player found with id ${matchData.getPlayer1}"
      player2 <- PlayerDao.getByPlayerName(matchData.getPlayer2) \/> s"No player found with id ${matchData.getPlayer2}"
      gameList = GameDao.getGameRecordsByBoutId(boutId)
      draft = Draft.fromDatabaseRecord(matchRes.into(Tables.DRAFT))
    } yield FullBoutRecord(matchRes, gameList, Map(player1.name -> player1, player2.name -> player2), draft)
  }

  def getBoutData(boutId: Int)(implicit dslContext: DSLContext): String \/ Bout = {
    for {
      boutRecord <- getById(boutId) \/> s"No match found with id $boutId"
      gameList <- GameDao.getGamesByBoutId(boutId)
    } yield Bout(gameList, BoutTypeEnum.fromInt(boutRecord.getBoutType))
  }

  def resetBout(id: Int)(implicit dslContext: DSLContext): Unit \/ Int = {
    val bout = getById(id)
    bout match {
      case Some(b) =>
        b.setStatus(0)
        b.setMatchUrl(null)
        b.setDraft(null)
        b.update().right
      case None => ().left
    }
  }

  def updateBoutDoubleForfeit(id: Int, text: String)(implicit  dslContext: DSLContext): Unit \/ Int = {
    val bout = getById(id)

    bout match {
      case Some(b) =>
        b.setStatus(2)
        b.setForfeitText(text)
        b.update().right
      case None => ().left
    }
  }

  def updateBoutForfeitStatus(id: Int, winner: String, text: String)(implicit dslContext: DSLContext): Unit \/ Int = {
    val bout = getById(id)
    bout match {
      case Some(b) =>
        b.setStatus(2)
        b.setForfeitWinner(winner)
        b.setForfeitText(text)
        b.update().right
      case None => ().left
    }
  }

  def markBoutAsPlayed(boutId: Int, url: String, draft: Option[DraftRecord])(implicit dslContext: DSLContext): String \/ Int = {
    val matchOption = getById(boutId)
    try {
      if (matchOption.isEmpty) {
        s"No match found with id $boutId".left
      } else {
        val theMatch = matchOption.get
        theMatch.setStatus(1)
        theMatch.setMatchUrl(url)
        if (draft.isDefined)
          theMatch.setDraft(draft.get.getId)
        theMatch.update().right
      }
    } catch {
      case e: Throwable => e.getMessage.left
    }
  }


}
