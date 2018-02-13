package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.MapOfTheWeekDao
import com.lthummus.sclmanager.database.dao.MapOfTheWeekDao.MapOfTheWeekEntry
import org.jooq.DSLContext
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.swagger.{Swagger, SwaggerSupport}

case class MapOfTheWeekData(homeMaps: List[MapOfTheWeekEntry])


class MapOfTheWeekServlet(implicit dslContext: DSLContext, val swagger: Swagger) extends SclManagerStack with JacksonJsonSupport with SwaggerSupport {
  override protected implicit def jsonFormats: Formats = DefaultFormats
  override protected def applicationDescription: String = "Gets the map of the week for the tournament"

  before() {
    contentType = formats("json")
  }

  private val getAll = (apiOperation[MapOfTheWeekData]("getAll")
    summary "Get league overview"
    notes "Returns nice summary data and a current snapshot of the league"
    )

  get("/", operation(getAll)) {
    MapOfTheWeekData(MapOfTheWeekDao.all())
  }

}
