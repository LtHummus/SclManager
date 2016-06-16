package com.lthummus.sclmanager

import com.lthummus.sclmanager.database.dao.LeagueDao
import org.jooq.DSLContext
import org.scalatra._
import zzz.generated.Tables

class SclManagerServlet(implicit dslContext: DSLContext) extends SclManagerStack {

  get("/") {
    Ok("nothing")
  }

}
