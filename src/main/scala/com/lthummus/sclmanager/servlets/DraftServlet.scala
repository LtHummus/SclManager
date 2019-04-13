package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.dao.DraftDao
import com.lthummus.sclmanager.scaffolding.SclManagerConfig
import com.lthummus.sclmanager.servlets.dto.DraftInput
import org.jooq.DSLContext
import org.json4s.ext.JodaTimeSerializers
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.swagger.{Swagger, SwaggerSupport}
import org.scalatra.{Forbidden, NotFound, Ok}
import org.slf4j.LoggerFactory

object DraftServlet {
  private val Logger = LoggerFactory.getLogger("DraftServlet")
}

class DraftServlet(implicit dslContext: DSLContext, val swagger: Swagger) extends SclManagerStack
  with JacksonJsonSupport
  with SwaggerSupport {

  import DraftServlet._

  protected val applicationDescription = "Gets draft information"

  protected implicit lazy val jsonFormats: Formats = DefaultFormats ++ JodaTimeSerializers.all

  private val sharedSecret = SclManagerConfig.sharedSecret

  before() {
    contentType = formats("json")
  }

  post("/report") {
    val auth = request.header("Authentication")
    auth match {
      case Some(x) if x == sharedSecret =>
        val input = parsedBody.camelizeKeys.extract[DraftInput]
        DraftDao.persist(input)

        Logger.info("Persisted draft from room {} between {} and {} successfully", input.roomCode, input.player1, input.player2)

        Ok(Map("error" -> "none"))

      case _ => Logger.warn("Invalid password for draft report"); Forbidden(Map("error" -> "Nope"))
    }
  }

  get("/:id") {
    val data = DraftDao.getById(params("id").toInt)
    data match {
      case None        => NotFound(Map("error" -> s"No draft with id ${params("id")} found"))
      case Some(draft) => Ok(draft)
    }
  }


}
