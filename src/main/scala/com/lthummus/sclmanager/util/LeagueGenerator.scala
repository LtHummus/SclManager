package com.lthummus.sclmanager.util

import com.lthummus.sclmanager.database.DatabaseConfigurator
import org.jooq.DSLContext
import zzz.generated.tables.records._
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

private object LeagueGenerator extends App {
  val Logger = LoggerFactory.getLogger(getClass)

  private case class Player(name: String, country: String)
  private case class League(order: Int, name: String, players: Seq[Player])
  private case class Match(player1: Player, player2: Player)

  private val Leagues = Seq(
    League(0, "Diamond", Seq(Player("krazycaley", "us"),
      Player("magician1099", "us"),
      Player("canadianbacon", "ca"),
      Player("kcmmmmm", "us"),
      Player("bloom", "au"),
      Player("cleetose", "us"))),

    League(1, "Platinum",
      Seq(Player("slappydavis", "us"),
        Player("varanas", "gb"),
        Player("sharper", "au"),
        Player("warningtrack", "us"),
        Player("drawnonward", "us"),
        Player("cameraman", "us"))),

    League(2, "Gold",
      Seq(Player("royalflush", "gb"),
        Player("pires", "pt"),
        Player("scallions", "us"),
        Player("james1221", "us"),
        Player("fearfulferret", "us"),
        Player("elvisnake", "us"))),

    League(3, "Silver",
      Seq(Player("sgnurf", "fr"),
        Player("wodar", "us"),
        Player("teetery", "us"),
        Player("checker", "us"),
        Player("falconhit", "ca"),
        Player("pwndnoob", "gb"))),

    League(4, "Bronze",
     Seq(Player("yerand", "lv"),
         Player("yeesh", "us"),
         Player("catnip", "lt"),
         Player("cbot", "au"),
         Player("jyaty", "sg"),
         Player("tarpshack", "us"))),

    League(5, "Copper",
      Seq(Player("bittersweet93", "gb"),
        Player("nanthelas", "us"),
        Player("progamingwithed", "us"),
        Player("trevor", "us"),
        Player("tge", "us"),
        Player("gmantsang", "ca"))),

    League(6, "Iron",
      Seq(Player("steph", "ca"),
        Player("marimo", "pl"),
        Player("baldrick", "pl"),
        Player("ml726", "us"),
        Player("arisumargatroid", "pt"),
        Player("poppe", "se"))),

    League(7, "Challenger",
      Seq(Player("danishspy", "dk"),
        Player("dburke", "us"),
        Player("fourliberties", "it"),
        Player("anorexicwhale", "us"),
        Player("quicklime", "us"),
        Player("moon", "us"),
        Player("sailormoon92", "us")))
  )
  val NumberOfTimesEveryonePlaysEachOther = 2

  implicit val Db: DSLContext = DatabaseConfigurator.getDslContext

  private def generatePairs(players: Seq[Player]): List[(Player, Player)] = {
    players.ensuring(_.length % 2 == 0)
    val reversedPlayers = players.reverse
    (for (x <- 0 until players.length / 2)
      yield (players(x), reversedPlayers(x))).toList
  }

  private def rotate(xs: Seq[Player], amount: Int) = xs.drop(amount) ++ xs.take(amount)

  private val ByeText = "BYE"

  private def generateWeeklySchedule(players: Seq[Player]) = {
    val playersWithByeIfNeeded = players.size % 2 match {
      case 0 => players
      case 1 => players ++ Seq(Player(ByeText, "XX"))
    }

    val shuffledPlayers = scala.util.Random.shuffle(playersWithByeIfNeeded)

    val anchor = Seq(shuffledPlayers.head)
    val everyoneElse = shuffledPlayers.tail

    val pairs = for (x <- 0 until shuffledPlayers.length - 1) yield
      generatePairs(anchor ++ rotate(everyoneElse, x))

    pairs.map(it => it.filterNot(containsBye))

  }

  private def containsBye(x: (Player, Player)): Boolean = x._1.name == ByeText || x._2.name == ByeText

  private def generateMatches(leagues: Seq[League]) = {
    leagues.map(it => (it, generateWeeklySchedule(it.players) ++ generateWeeklySchedule(it.players)))
  }

  private def populateWeekMatches(league: League, week: Int, matches: Iterable[(Player, Player)]) = {
    Logger.info(s"populating matches for ${league.name} week $week: ")
    for (curr <- matches) {
      Logger.info(s"${curr._1} vs. ${curr._2}")
    }
  }

  private def persistLeagueData(league: League, matches: Seq[List[(Player, Player)]]) = {
    //step 1: create the league

    val leagueRecord = new DivisionRecord(league.name, league.order)
    Db.executeInsert(leagueRecord)

    //step 2: insert the players
    val playerRecords = league.players.map(p => new PlayerRecord(p.name, league.name, 0, 0, 0, p.country))
    Db.batchInsert(playerRecords.asJava).execute()

    //step 3: insert all the matches
    for (week <- matches.indices) {
      val thisWeek = matches(week)
      val matchRecords = thisWeek.map(it => new BoutRecord(null, week + 1, league.name, it._1.name, it._2.name, 0, null, null, null))
      Db.batchInsert(matchRecords.asJava).execute()
    }
  }

  private val allLeaguesXMatches = generateMatches(Leagues)

  for {(league, matches) <- allLeaguesXMatches} {
    persistLeagueData(league, matches)
  }

}
