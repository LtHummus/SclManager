package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.BoutDao
import org.jooq.DSLContext
import org.scalatra.NotFound

class DownloadServlet(implicit dslContext: DSLContext) extends SclManagerStack {

  get("/:id") {
    val potentialUrl = for {
      record <- BoutDao.getById(params("id").toInt)
      url    <- Option(record.getMatchUrl)
    } yield url

    potentialUrl match {
      case None => NotFound("Not found -- 404")
      case Some(url) => redirect(url)
    }
  }
}
