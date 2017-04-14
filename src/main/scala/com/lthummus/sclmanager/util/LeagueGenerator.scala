package com.lthummus.sclmanager.util

import com.lthummus.sclmanager.database.DatabaseConfigurator
import org.jooq.DSLContext
import zzz.generated.tables.records._
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

object LeagueGenerator extends App {
  val Logger = LoggerFactory.getLogger(getClass)

  type PlayerName = String
  case class League(order: Int, name: String, players: Seq[PlayerName])
  case class Match(player1: PlayerName, player2: PlayerName)

  val Leagues = Seq(
    League(0, "Diamond", Seq("krazycaley", "magician1099", "canadianbacon", "kcmmmmm", "bloom", "cleetose")),
    League(1, "Platinum", Seq("slappydavis", "varanas", "sharper", "warningtrack", "drawnonward", "cameraman")),
    League(2, "Gold", Seq("royalflush", "pires", "scallions", "james1221", "fearfulferret", "elvisnake")),
    League(3, "Silver", Seq("sgnurf", "wodar", "teetery", "checker", "falconhit", "pwndnoob")),
    League(4, "Bronze", Seq("yerand", "yeesh", "catnip", "cbot", "jyaty", "tarpshack")),
    League(5, "Copper", Seq("bittersweet93", "nanthelas", "progamingwithed", "trevor", "tge", "gmantsang")),
    League(6, "Iron", Seq("steph", "marimo", "baldrick", "ml726", "arisumargatroid", "poppe")),
    League(7, "Challenger", Seq("danishspy", "dburke", "fourliberties", "anorexicwhale", "quicklime", "moon", "sailormoon92"))
  )
  val NumberOfTimesEveryonePlaysEachOther = 2

  implicit val Db: DSLContext = DatabaseConfigurator.getDslContext

  def generatePairs(players: Seq[PlayerName]): List[(PlayerName, PlayerName)] = {
    players.ensuring(_.length % 2 == 0)
    val reversedPlayers = players.reverse
    (for (x <- 0 until players.length / 2)
      yield (players(x), reversedPlayers(x))).toList
  }

  private def rotate(xs: Seq[PlayerName], amount: Int) = xs.drop(amount) ++ xs.take(amount)

  private val ByeText = "BYE"

  def generateWeeklySchedule(players: Seq[PlayerName]) = {
    val playersWithByeIfNeeded = players.size % 2 match {
      case 0 => players
      case 1 => players ++ Seq(ByeText)
    }

    val shuffledPlayers = scala.util.Random.shuffle(playersWithByeIfNeeded)

    val anchor = Seq(shuffledPlayers.head)
    val everyoneElse = shuffledPlayers.tail

    val pairs = for (x <- 0 until shuffledPlayers.length - 1) yield
      generatePairs(anchor ++ rotate(everyoneElse, x))

    pairs.map(it => it.filterNot(containsBye))

  }

  private def containsBye(x: (String, String)): Boolean = x._1 == ByeText || x._2 == ByeText

  def generateMatches(leagues: Seq[League]) = {
    leagues.map(it => (it, generateWeeklySchedule(it.players) ++ generateWeeklySchedule(it.players)))
  }

  def populateWeekMatches(league: League, week: Int, matches: Iterable[(PlayerName, PlayerName)]) = {
    Logger.info(s"populating matches for ${league.name} week $week: ")
    for (curr <- matches) {
      Logger.info(s"${curr._1} vs. ${curr._2}")
    }
  }

  def persistLeagueData(league: League, matches: Seq[List[(String, String)]]) = {
    //step 1: create the league

    val leagueRecord = new DivisionRecord(league.name, league.order)
    Db.executeInsert(leagueRecord)

    //step 2: insert the players
    val playerRecords = league.players.map(new PlayerRecord(_, league.name, 0, 0, 0, "sp"))
    Db.batchInsert(playerRecords.asJava).execute()

    //step 3: insert all the matches
    for (week <- matches.indices) {
      val thisWeek = matches(week)
      val matchRecords = thisWeek.map(it => new BoutRecord(null, week + 1, league.name, it._1, it._2, 0, null, null, null))
      Db.batchInsert(matchRecords.asJava).execute()
    }
  }

  val allLeaguesXMatches = generateMatches(Leagues)

  for {(league, matches) <- allLeaguesXMatches} {
    persistLeagueData(league, matches)
  }

}
