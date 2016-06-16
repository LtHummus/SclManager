package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.LeagueDao
import org.jooq.DSLContext
import org.scalatra.Ok


class LeagueServlet(implicit dslContext: DSLContext) extends SclManagerStack {
  get("/") {
    Ok(LeagueDao.all())
  }
}
