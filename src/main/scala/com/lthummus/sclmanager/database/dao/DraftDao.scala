package com.lthummus.sclmanager.database.dao

import org.jooq.DSLContext
import zzz.generated.Tables

import scala.collection.JavaConversions._

object DraftDao {

  def all()(implicit dslContext: DSLContext) = {
    dslContext.selectFrom(Tables.DRAFT).fetch().toList
  }

  def getById(id: Int)(implicit dslContext: DSLContext) = {
    val res = dslContext.selectFrom(Tables.DRAFT).where(Tables.DRAFT.ID.eq(id)).fetch

    res.size() match {
      case 0 => None
      case 1 => Some(res.get(0))
      case _ => ???
    }
  }

}
