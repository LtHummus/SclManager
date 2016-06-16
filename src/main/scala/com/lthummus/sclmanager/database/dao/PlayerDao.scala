package com.lthummus.sclmanager.database.dao

import com.lthummus.sclmanager.database.data.Player
import org.jooq.DSLContext
import zzz.generated.Tables

import scala.collection.JavaConversions._

object PlayerDao {

  def all()(implicit dslContext: DSLContext) = {
    dslContext.selectFrom(Tables.PLAYER).fetch().toList.map(Player.fromDatabaseRecord)
  }

  def getById(id: Int)(implicit dslContext: DSLContext): Option[Player] = {
    val res = dslContext.selectFrom(Tables.PLAYER).where(Tables.PLAYER.ID.eq(id)).fetch()

    res.size() match {
      case 0 => None
      case _ => Some(Player.fromDatabaseRecord(res.get(0)))
    }
  }

}
