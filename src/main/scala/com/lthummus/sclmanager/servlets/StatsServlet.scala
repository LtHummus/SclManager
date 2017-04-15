package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.{GameDao, PlayerDao}
import com.lthummus.sclmanager.database.dao.GameDao._
import com.lthummus.sclmanager.database.dao.PlayerDao._
import org.jooq.DSLContext
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.Ok
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.swagger.{Swagger, SwaggerSupport}
import zzz.generated.tables.records.PlayerRecord

import scalaz.\/-



class StatsServlet(implicit dslContext: DSLContext, val swagger: Swagger) extends SclManagerStack
  with JacksonJsonSupport with SwaggerSupport {

  before() {
    contentType = formats("json")
  }

  override protected def applicationDescription: String = "Some stats"

  override protected implicit def jsonFormats: Formats = DefaultFormats

  get("/") {
    val games = GameDao.all.map(_.asReplay).collect{ case \/-(it) => it}

    val numberOfGamesPlayed = games.length
    val resultCounts = games.groupBy(_.result).map{case (key, value) => (key.toString, value.length)}
    val mapCounts = games.groupBy(_.level).map{case (key, value) => (key.name.toString, value.length)}



    Ok(Map("numberOfGamesPlayed" -> numberOfGamesPlayed,
      "resultCounts" -> resultCounts,
      "mapCounts" -> mapCounts))
  }
}
