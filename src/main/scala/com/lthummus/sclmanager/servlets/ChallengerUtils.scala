package com.lthummus.sclmanager.servlets

import com.lthummus.sclmanager.SclManagerStack
import com.lthummus.sclmanager.database.TransactionSupport
import com.lthummus.sclmanager.database.dao.DivisionDao._
import com.lthummus.sclmanager.database.dao.{BoutDao, DivisionDao, PlayerDao}
import com.lthummus.sclmanager.scaffolding.SclManagerConfig
import com.lthummus.sclmanager.servlets.dto.NewMatchesInput
import org.jooq.DSLContext
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.servlet.FileUploadSupport
import org.scalatra.swagger.{Swagger, SwaggerSupport}
import org.scalatra.{BadRequest, Ok, Unauthorized}
import zzz.generated.tables.records.{BoutRecord, PlayerRecord}

import scala.collection.JavaConverters._
import scala.util.Random


class ChallengerUtils(implicit dslContext: DSLContext, val swagger: Swagger)
  extends SclManagerStack
  with JacksonJsonSupport
  with FileUploadSupport
  with SwaggerSupport
  with TransactionSupport {

  override protected def applicationDescription: String = "Utility for challenger division"

  override protected implicit def jsonFormats: Formats = DefaultFormats

  private def pairToBoutRecord(names: (String, String), week: Int, division: String) = {
    new BoutRecord(null, week, division, names._1, names._2, 0, null, null, null, 0, null, null)
  }

  private case class PlayerMatchingData(name: String, active: Boolean, wins: Int, losses: Int, draws: Int, score: Int) {
    def scoreString: String = s"$wins-$losses-$draws"
  }

  private object PlayerMatchingData {
    def apply(x: PlayerRecord): PlayerMatchingData = {
      PlayerMatchingData(x.getName, x.isActive, x.getWins, x.getLosses, x.getDraws, 2 * x.getWins + x.getDraws)
    }
  }

  private val sharedSecret = SclManagerConfig.sharedSecret

  before() {
    contentType = formats("json")
  }

  post("/create") {
    val data = parsedBody.extract[NewMatchesInput]

    val matchesToAdd = data.matchPairs
    val divisionPlayers = PlayerDao.getPlayersInDivision(data.effectiveDivision).map(_.name).toSet
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
    } else if (data.password != sharedSecret) {
      Unauthorized(Map("error" -> "Shared Secret Incorrect", "detail" -> "But all matches would have been persisted properly"))
    } else {
      val matchRecords = matchesToAdd.map(pairToBoutRecord(_, data.week, data.effectiveDivision))
      val recordsCreated = dslContext.batchInsert(matchRecords.asJava).execute()
      Map("week" -> data.week, "matches" -> matchesToAdd.map{ case(a, b) => s"$a v $b"}, "division" -> data.effectiveDivision, "records_created" -> recordsCreated.size)
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
      val players = DivisionDao.getParticipatingPlayersInDivision(division).map(PlayerMatchingData(_))

      val inactivePlayers = players.filterNot(_.active)
      val activePlayers = players.filter(_.active)

      val activePlayersByScore = activePlayers.groupBy(_.score).mapValues(_.map(_.name))
      val activeMatchPairs = activePlayers
        .groupBy(_.score)
        .mapValues(Random.shuffle(_))
        .toList
        .sortBy(_._1)
        .reverse
        .flatMap{ case (_, l) => l.map(_.name)}
        .grouped(2)
        .map(x => s"${x(0)},${if (x.length == 1) "BYE" else x(1)}")
        .toList

      val inactiveMatchPairs = generateMatches(inactivePlayers.map(_.name))


      Ok(Map("active_players_by_score" -> activePlayersByScore,
        "matches" -> activeMatchPairs,
        "inactive_players" -> inactivePlayers.map(_.name),
        "inactive_matches" -> inactiveMatchPairs))
    }
  }
}
