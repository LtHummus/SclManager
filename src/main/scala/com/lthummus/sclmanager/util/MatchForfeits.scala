package com.lthummus.sclmanager.util

import com.lthummus.sclmanager.database.dao.{BoutDao, PlayerDao}
import zzz.generated.tables.records.BoutRecord
import scalaz._
import Scalaz._
import com.lthummus.sclmanager.database.TransactionSupport
import org.jooq.DSLContext
import org.slf4j.LoggerFactory

object MatchForfeits extends TransactionSupport {
  private val Logger = LoggerFactory.getLogger("MatchForfeits")

  private def checkStatus(bout: BoutRecord) = {
    if (bout.getStatus == 0) {
      ().right
    } else {
      Logger.warn("Attempting to forfeit played/forfeited match {}", bout.getId)
      
      "Match has already been played or forfeited".left
    }
  }

  def forfeitMatch(id: Int, winner: String, text: String)(implicit dslContext: DSLContext) = {
    def winnerIsValid(winner: String, player1Name: String, player2Name: String) = {
      if (winner == player1Name || winner == player2Name)
        ().right
      else
        s"$winner is not valid for this match".left
    }

    insideTransaction { implicit dslContext =>
      for {
        bout <- BoutDao.getById(id) \/> "There is no match with that id"
        _    <- winnerIsValid(winner, bout.getPlayer1, bout.getPlayer2)
        _    <- checkStatus(bout)
        _    <- BoutDao.updateBoutForfeitStatus(id, winner, text)
        _    <- PlayerDao.postResult(bout.getPlayer1, if (bout.getPlayer1 == winner) "win" else "loss")
        _    <- PlayerDao.postResult(bout.getPlayer2, if (bout.getPlayer2 == winner) "win" else "loss")
      } yield {
        Logger.info("Successfully forfeited match {}", id)
        bout
      }
    }
  }

  def doubleForfeit(id: Int, text: String)(implicit dslContext: DSLContext) = {
    insideTransaction { implicit dslContext =>
      for {
        bout <- BoutDao.getById(id) \/> "No bout found with id"
        _    <- checkStatus(bout)
        _    <- BoutDao.updateBoutDoubleForfeit(id, text)
        _    <- PlayerDao.postResult(bout.getPlayer1, "loss")
        _    <- PlayerDao.postResult(bout.getPlayer2, "loss")
      } yield {
        Logger.info("Successfully double forfeited match {}", id)

        bout
      }
    }
  }
}
