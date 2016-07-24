package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.DraftDao
import com.lthummus.sclmanager.servlets.dto.{Draft, DraftInput}
import org.jooq.DSLContext
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.Ok
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.swagger.{Swagger, SwaggerSupport}


class DraftServlet(implicit dslContext: DSLContext, val swagger: Swagger) extends SclManagerStack
  with JacksonJsonSupport
  with SwaggerSupport {


  protected val applicationDescription = "Gets draft information"

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }


  post("/report") {
    val input = parsedBody.camelizeKeys.extract[DraftInput]

    DraftDao.persist(input)

    Ok("ok")
  }


}
