package com.lthummus.sclmanager

import org.jooq.DSLContext
import org.scalatra._
import zzz.generated.Tables

class SclManagerServlet(implicit dslContext: DSLContext) extends SclManagerStack {

  get("/") {
    val leagues = dslContext.selectFrom(Tables.LEAGUE).fetch()

    leagues.size() match {
      case 0 => NotFound("No leagues found")
      case _ => Ok("First league is " + leagues.get(0).getName)
    }
  }

}
