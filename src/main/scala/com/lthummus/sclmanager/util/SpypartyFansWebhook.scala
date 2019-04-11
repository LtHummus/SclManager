package com.lthummus.sclmanager.util

import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import com.lthummus.sclmanager.parsing.Bout
import com.lthummus.sclmanager.scaffolding.SclManagerConfig
import com.lthummus.sclmanager.servlets.dto.Match
import org.json4s.{DefaultFormats, Formats}
import org.slf4j.LoggerFactory
import scalaj.http.Http

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


object SpypartyFansWebhook {

  private val Logger = LoggerFactory.getLogger("SpyPartyFansWebhook")

  private implicit val formats: Formats = DefaultFormats
  private val HookUrl = SclManagerConfig.spypartyFansHookUrl

  def postToWebhook(bout: Match): Unit = {
    Future {
      val payload = write(bout)
      val req = Http(HookUrl)
        .header("Content-Type", "application/json")
        .postData(payload)
      val res = req.asString

      if (!res.is2xx) {
        Logger.warn("Got HTTP status code {} from SPF. Body: {}", res.code, res.body)
      } else {
        Logger.info("Match {} uploaded to SpyParty Fans properly", bout.id)
      }
    }
  }
}
