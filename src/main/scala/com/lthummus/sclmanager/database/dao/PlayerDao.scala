package com.lthummus.sclmanager.database.dao

import org.jooq.DSLContext
import zzz.generated.Tables
import zzz.generated.tables.records.PlayerRecord

import scala.collection.JavaConversions._

import scalaz._
import Scalaz._

object PlayerDao {

  def all()(implicit dslContext: DSLContext) = {
    dslContext.selectFrom(Tables.PLAYER).fetch().toList
  }

  def getByPlayerName(name: String)(implicit dslContext: DSLContext): Option[PlayerRecord] = {
    val res = dslContext.selectFrom(Tables.PLAYER).where(Tables.PLAYER.NAME.eq(name)).fetch()

    res.size() match {
      case 0 => None
      case _ => Some(res.get(0))
    }
  }

  def getNumberOfBoutsPlayed(name: String)(implicit dslContext: DSLContext): Int = {
    dslContext
      .selectCount()
      .from(Tables.BOUT)
      .where(Tables.BOUT.PLAYER1.eq(name))
      .or(Tables.BOUT.PLAYER2.eq(name)).fetchOne(0, classOf[Int])
  }

  def postResult(name: String, result: String)(implicit dslContext: DSLContext): String \/ PlayerRecord = {
    val player = getByPlayerName(name)

    //TODO: update this from config
    if (player.isEmpty) {
      s"No player found with name $name".left
    } else {
      val unwrappedPlayer = player.get
      result match {
        case "win" => unwrappedPlayer.setWins(unwrappedPlayer.getWins + 1)
        case "draw" => unwrappedPlayer.setDraws(unwrappedPlayer.getDraws + 1)
        case "loss" => unwrappedPlayer.setLosses(unwrappedPlayer.getLosses + 1)
      }

      unwrappedPlayer.store()
      unwrappedPlayer.right
    }

  }

}
