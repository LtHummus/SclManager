package com.lthummus.sclmanager.database.dao

import com.lthummus.sclmanager.parsing.Bout
import org.jooq.DSLContext
import zzz.generated.Tables
import zzz.generated.tables.records.{BoutRecord, DraftRecord, GameRecord, PlayerRecord}

import scala.collection.JavaConversions._
import scalaz._
import Scalaz._


case class FullBoutRecord(bout: BoutRecord, games: List[GameRecord], playerMap: Map[String, PlayerRecord], draft: Option[DraftRecord])

object BoutDao {

  def getAll()(implicit dslContext: DSLContext) = dslContext.selectFrom(Tables.BOUT).fetch().toList

  def getById(id: Int)(implicit dslContext: DSLContext): Option[BoutRecord] = {
    val res = dslContext.selectFrom(Tables.BOUT).where(Tables.BOUT.ID.eq(id)).fetch()

    res.size() match {
      case 0 => None
      case _ => Some(res.get(0))
    }
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

  def getNextToBePlayedByPlayers(player1: String, player2: String)(implicit dslContext: DSLContext) = {
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
      matchData <- getById(boutId) \/> s"No match found with id $boutId"
      player1 <- PlayerDao.getByPlayerName(matchData.getPlayer1) \/> s"No player found with id ${matchData.getPlayer1}"
      player2 <- PlayerDao.getByPlayerName(matchData.getPlayer2) \/> s"No player found with id ${matchData.getPlayer2}"
      gameList = GameDao.getGameRecordsByBoutId(boutId)
      draft = if (matchData.getDraft != null) DraftDao.getById(matchData.getDraft) else None
    } yield FullBoutRecord(matchData, gameList, Map(player1.getName -> player1, player2.getName -> player2), draft)
  }

  def getBoutData(boutId: Int)(implicit dslContext: DSLContext): String \/ Bout = {
    for {
      matchData <- getById(boutId) \/> s"No match found with id $boutId"
      player1 <- PlayerDao.getByPlayerName(matchData.getPlayer1) \/> s"No player found with id ${matchData.getPlayer1}"
      player2 <- PlayerDao.getByPlayerName(matchData.getPlayer2) \/> s"No player found with id ${matchData.getPlayer2}"
      gameList <- GameDao.getGamesByBoutId(boutId)
    } yield Bout(gameList)
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
