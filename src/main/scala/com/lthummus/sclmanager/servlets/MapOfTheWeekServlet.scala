package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.MapOfTheWeekDao
import com.lthummus.sclmanager.scaffolding.SystemConfig._
import com.lthummus.sclmanager.servlets.dto.{MapOfTheWeekData, MapOfTheWeekEntry}
import com.typesafe.config.{Config, ConfigFactory}
import org.jooq.DSLContext
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.swagger.{Swagger, SwaggerSupport}



class MapOfTheWeekServlet(implicit dslContext: DSLContext, val swagger: Swagger) extends SclManagerStack with JacksonJsonSupport with SwaggerSupport {
  override protected implicit def jsonFormats: Formats = DefaultFormats
  override protected def applicationDescription: String = "Gets the map of the week for the tournament"

  private lazy val NumberOfWeeks = ConfigFactory.load().getIntWithStage("tournament.numWeeks")
  private lazy val MapsOfTheWeek = generateMapsOfTheWeek()

  before() {
    contentType = formats("json")
  }

  private def fillInHomeMaps(known: List[MapOfTheWeekEntry]) = {
    val mapMap = known.groupBy(_.week) /* OH GOD WHY */

    (1 to NumberOfWeeks).toList.map { week =>  /* IT'S EVEN WORSE! */
      mapMap.get(week) match {
        case Some(entry) => entry.head
        case None        => MapOfTheWeekEntry.defaultForWeek(week)
      }
    }
  }

  private def generateMapsOfTheWeek(): MapOfTheWeekData = {
    MapOfTheWeekData(fillInHomeMaps(MapOfTheWeekDao.all()))
  }

  private val getAll = (apiOperation[MapOfTheWeekData]("getAll")
    summary "Get league overview"
    notes "Returns nice summary data and a current snapshot of the league"
    )



  get("/", operation(getAll)) {
    MapsOfTheWeek
  }

}
