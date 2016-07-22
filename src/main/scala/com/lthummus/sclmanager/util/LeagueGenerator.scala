package com.lthummus.sclmanager.util

import com.lthummus.sclmanager.database.DatabaseConfigurator
import com.lthummus.sclmanager.database.dao.{DivisionDao, PlayerDao}
import org.jooq.DSLContext
import zzz.generated.tables.records._
import zzz.generated.Tables

import scala.collection.JavaConverters._

object LeagueGenerator extends App {
  type PlayerName = String
  case class League(order: Int, name: String, players: Seq[PlayerName])
  case class Match(player1: PlayerName, player2: PlayerName)

  val Leagues = Seq(
    League(0, "Diamond", Seq("magician1099", "krazycaley", "canadianbacon", "kcmmmmm", "bloom", "scientist")),
    League(1, "Platinum", Seq("cleetose", "slappydavis", "kikar", "sharper", "varanas", "royalflush")),
    League(2, "Gold", Seq("warningtrack", "pires", "cameraman", "scallions", "james1221", "briguy")),
    League(3, "Silver", Seq("fearfulferret", "lthummus", "elvisnake", "iceman", "sgnurf", "checker"))
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

  def generateWeeklySchedule(players: Seq[PlayerName]) = {
    val shuffledPlayers = scala.util.Random.shuffle(players)

    val anchor = Seq(shuffledPlayers.head)
    val everyoneElse = shuffledPlayers.tail

    for (x <- 0 until shuffledPlayers.length - 1) yield
      generatePairs(anchor ++ rotate(everyoneElse, x))

  }

  def generateMatches(leagues: Seq[League]) = {
    leagues.map(it => (it, generateWeeklySchedule(it.players) ++ generateWeeklySchedule(it.players)))
  }

  def populateWeekMatches(league: League, week: Int, matches: Iterable[(PlayerName, PlayerName)]) = {
    println(s"populating matches for ${league.name} week $week: ")
    for (curr <- matches) {
      println(s"\t${curr._1} vs. ${curr._2}")
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
