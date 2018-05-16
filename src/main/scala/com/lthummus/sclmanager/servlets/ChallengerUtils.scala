package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.TransactionSupport
import com.lthummus.sclmanager.database.dao.{BoutDao, DivisionDao}
import com.lthummus.sclmanager.servlets.dto.NewMatchesInput
import org.jooq.DSLContext
import org.json4s.{DefaultFormats, Formats}
import org.omg.CORBA.BAD_INV_ORDER
import org.scalatra.{BadRequest, Ok, Unauthorized}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.servlet.FileUploadSupport
import org.scalatra.swagger.{Swagger, SwaggerSupport}
import zzz.generated.tables.records.BoutRecord
import scalaz._
import Scalaz._
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._
import scala.util.Random

import com.lthummus.sclmanager.scaffolding.SystemConfig._


class ChallengerUtils(implicit dslContext: DSLContext, val swagger: Swagger)
  extends SclManagerStack
  with JacksonJsonSupport
  with FileUploadSupport
  with SwaggerSupport
  with TransactionSupport {

  override protected def applicationDescription: String = "Utility for challenger division"

  override protected implicit def jsonFormats: Formats = DefaultFormats

  private val sharedSecret = ConfigFactory.load().getEncryptedString("sharedSecret")

  before() {
    contentType = formats("json")
  }


  post("/create") {
    def pairToBoutRecord(names: (String, String), week: Int, division: String) = {
      new BoutRecord(null, week, division, names._1, names._2, 0, null, null, null, 0, null, null)
    }

    val data = parsedBody.extract[NewMatchesInput]

    if (data.password != sharedSecret) {
      Unauthorized(Map("error" -> "Wrong password", "detail" -> "Password is incorrect"))
    } else {
      val matchesToAdd = data.matchPairs
      val divisionPlayers = DivisionDao.getPlayersInDivision(data.effectiveDivision).map(_.getName).toSet
      val requestedPlayers = matchesToAdd.flatMap{case (a, b) => List(a, b)}.toSet

      val unknownPlayers = requestedPlayers -- divisionPlayers

      val existingMatches = BoutDao.getNormalizedMatchesByDivision(data.effectiveDivision).toSet
      val newMatchSet = matchesToAdd.toSet

      val duplicateMatches = existingMatches.intersect(newMatchSet)

      if (divisionPlayers.isEmpty) {
        BadRequest(Map("error" -> s"Unknown division", "detail" -> s"Unknown division: ${data.effectiveDivision}"))
      } else if (unknownPlayers.nonEmpty) {
        BadRequest(Map("error" -> "Unknown players", "detail" -> unknownPlayers.toList.sorted))
      } else if (duplicateMatches.nonEmpty) {
        BadRequest(Map("error" -> "Duplicate Matches", "detail" -> duplicateMatches.toList.map{ case (a, b) => s"$a/$b"}))
      } else {
        val matchRecords = matchesToAdd.map(pairToBoutRecord(_, data.week, data.effectiveDivision))
        val recordsCreated = dslContext.batchInsert(matchRecords.asJava).execute()
        Map("week" -> data.week, "matches" -> matchesToAdd.map{ case(a, b) => s"$a v $b"}, "division" -> data.effectiveDivision, "records_created" -> recordsCreated.size)
      }
    }
  }

  get("/swiss/:division") {

    def generateMatches(names: List[String]) = {
      val finalNameList = if (names.size % 2 != 0) {
        names :+ "BYE"
      } else {
        names
      }
      Random.shuffle(finalNameList).grouped(2).map(x => s"${x(0)},${x(1)}").toList

    }

    val division = params("division")
    if (division.isEmpty) {
      BadRequest(Map("error" -> "Unknown division"))
    } else {
      val players = DivisionDao.getPlayersInDivision(division)

      val playerScores = players.map{ p =>
        val scoreString = s"${p.getWins}-${p.getLosses}-${p.getDraws}"
        (p.getName, scoreString)
      }

      val playersByScore = playerScores.groupBy(_._2).mapValues(_.map(_._1))

      val allMatchPairs = playersByScore.mapValues(generateMatches)

      Ok(Map("pairing_scores" -> allMatchPairs, "matches" -> allMatchPairs.values.flatten))
    }
  }
}
