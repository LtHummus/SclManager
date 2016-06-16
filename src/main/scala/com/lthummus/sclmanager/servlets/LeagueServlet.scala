package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.LeagueDao
import org.jooq.DSLContext
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.Ok
import org.scalatra.json.JacksonJsonSupport


class LeagueServlet(implicit dslContext: DSLContext) extends SclManagerStack with JacksonJsonSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  get("/") {
    Ok(LeagueDao.all())
  }
}
