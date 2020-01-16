package com.lthummus.sclmanager.util

import com.lthummus.sclmanager.scaffolding.SclManagerConfig
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import com.lthummus.sclmanager.servlets.dto.Match
import org.slf4j.LoggerFactory
import scalaj.http.Http

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object DiscordPoster {

  private val Logger = LoggerFactory.getLogger("DiscordPoster")

  private val HookUrl = SclManagerConfig.discordResultsWebhook

  def post(bout: Match): Unit = {
    Future {
      Logger.info("Posting bout {} results to Discord", bout.id)

      val payload = compact(render("content" -> bout.discordPost))
      val req = Http(HookUrl)
        .postData(payload)
        .header("content-type", "application/json")

      req.asString
    }
  }
}
