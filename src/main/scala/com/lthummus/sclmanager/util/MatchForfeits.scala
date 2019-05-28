package com.lthummus.sclmanager.util

import com.lthummus.sclmanager.database.dao.{BoutDao, PlayerDao}
import zzz.generated.tables.records.BoutRecord
import scalaz._
import Scalaz._
import com.lthummus.sclmanager.database.TransactionSupport
import com.lthummus.sclmanager.parsing.{Bout, BoutTypeEnum}
import com.lthummus.sclmanager.servlets.dto.Match
import org.jooq.DSLContext
import org.slf4j.LoggerFactory

object MatchForfeits extends TransactionSupport {
  private val Logger = LoggerFactory.getLogger("MatchForfeits")

  private def checkStatusAndUndoIfNeeded(bout: BoutRecord)(implicit dslContext: DSLContext) = {
    if (bout.getStatus == 0) {
      ().right
    } else if (bout.getStatus == 1) {
      Logger.info("Detected {} has been played. Undoing it.", bout.getId)
      undoMatch(bout)
      ().right
    } else {
      Logger.warn("Attempting to forfeit played/forfeited match {}", bout.getId)
      "Match has already been played or forfeited".left
    }
  }

  private def undoMatch(bout: BoutRecord)(implicit dslContext: DSLContext) = {

    Logger.info("Undoing match {}", bout.getId)
    val parsedMatch = for {
      fullBout <- BoutDao.getFullBoutRecords(bout.getId)
    } yield {
      Match.fromDatabaseRecordWithGames(fullBout.bout, fullBout.games, fullBout.playerMap, fullBout.draft)
    }

    parsedMatch match {
      case -\/(error) => Logger.error("Error getting match data: {}", error); throw new Exception(s"Error getting bout: $error")
      case \/-(data) =>
        //we're duplicating a bunch of logic here...sigh
        if (data.games.nonEmpty) {
          val allGames = data.games.get

          val bout = Bout(allGames.map(_.asReplay), BoutTypeEnum.fromString(data.matchType))

          if (bout.isTie) {
            //remove tie from both

            Logger.info("Bout is a tie. Removing tie from both players")
            PlayerDao.removeResult(data.player1.name, "draw")
            PlayerDao.removeResult(data.player2.name, "draw")
          } else {
            require(bout.player1Score != bout.player2Score)
            val winner = if (bout.player1Score > bout.player2Score) bout.player1 else bout.player2
            val loser = if (bout.player1Score > bout.player2Score) bout.player2 else bout.player1

            Logger.info("Detected {} as winner and {} as loser", Array[AnyRef](winner, loser): _*)

            PlayerDao.removeResult(winner, "win")
            Logger.info("Removed win from {}", winner)

            PlayerDao.removeResult(loser, "loss")
            Logger.info("Removed loss from {}", loser)
          }
        } else {
          Logger.error("Bout {} has been marked as played but has no games", bout.getId)
          throw new Exception(s"Bout ${bout.getId} has been played...but has no games!?")
        }

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
        _    <- checkStatusAndUndoIfNeeded(bout)
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
        _    <- checkStatusAndUndoIfNeeded(bout)
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
