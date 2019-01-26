package com.lthummus.sclmanager.util

import com.lthummus.sclmanager.database.DatabaseConfigurator
import org.jooq.DSLContext
import zzz.generated.tables.records._
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

private object League4Generator extends App {
  val Logger = LoggerFactory.getLogger(getClass)

  private case class Player(var name: String, var replayName: Option[String], country: Option[String]) {
    name = name.toLowerCase
    replayName = replayName.map(_.toLowerCase)
  }
  private case class League(order: Int, name: String, players: Seq[Player])
  private case class Match(player1: Player, player2: Player)

  private val Leagues = Seq(League(0, "Diamond", Seq(Player("KrazyCaley", None, None),
    Player("KCMmmmm", None, None),
    Player("Bloom", None, None),
    Player("CanadianBacon", None, None),
    Player("Magician1099", None, None),
    Player("WarningTrack", None, None))),

    League(1, "Platinum", Seq(Player("Cleetose", None, None),
      Player("Slappydavis", None, None),
      Player("Varanas", None, None),
      Player("Drawnonward", None, None),
      Player("Cameraman", None, None),
      Player("Pires", None, None))),


    League(2, "Gold", Seq(Player("Sharper", None, None),
      Player("Scallions", None, None),
      Player("Elvisnake", None, None),
      Player("RoyalFlush", None, None),
      Player("Falconhit", None, None),
      Player("Pwndnoob", None, None))),


    League(3, "Silver", Seq(Player("Turnout8", None, None),
      Player("James1221", None, None),
      Player("Wodar", None, None),
      Player("Teetery", None, None),
      Player("Yerand", None, None),
      Player("Checker", None, None))),


    League(4, "Brozne", Seq(Player("Sgnurf", None, None),
      Player("Jyaty", None, None),
      Player("Catnip", None, None),
      Player("Cbot", None, None),
      Player("Nanthelas", None, None),
      Player("Arturiax", None, None))),

    League(5, "Copper", Seq(Player("Bittersweet93", None, None),
      Player("Gmantsang", None, None),
      Player("Trevor", None, None),
      Player("Marimo", None, None),
      Player("Baldrick", None, None),
      Player("Steph", None, None))),

    League(6, "Iron", Seq(Player("ml726", None, None),
      Player("Fourliberties", None, None),
      Player("AnorexicWhale", None, None),
      Player("Jecat", None, None),
      Player("Mrrgrs", None, None),
      Player("Strobo", None, None))),

    League(7, "Challenger", Seq(Player("Soolseem", None, None),
      Player("VentusKitsune", None, None),
      Player("DarkerSolstice", None, None),
      Player("Bitbandingpig", None, None),
      Player("Waterhouse", None, None),
      Player("Quicklime", None, None),
      Player("Danishspy", None, None),
      Player("Clouseau", None, None),
      Player("Motionblur", None, None),
      Player("Amlabella", None, None),
      Player("Jackoburst", None, None),
      Player("Tristram", None, None),
      Player("Isauragard", None, None),
      Player("Sikeeatric", None, None),
      Player("Pash1k", None, None),
      Player("Yoric", None, None),
      Player("Fancypants ", None, None),
      Player("Hellno", None, None),
      Player("Magicdoer1", None, None),
      Player("Essem", None, None),
      Player("Brskaylor", None, None),
      Player("Portalfreek", None, None),
      Player("Zerodoom", None, None),
      Player("xryanmacx", None, None),
      Player("And", None, None),
      Player("Bayushi", None, None),
      Player("Cronk", None, None),
      Player("Humankirby", None, None),
      Player("Butterscotch", None, None),
      Player("Pox", None, None),
      Player("Davidw", None, None),
      Player("Thealpacalypse", None, None),
      Player("Jaylez", None, None),
      Player("Tarekmak", None, None),
      Player("Jinetic", None, None),
      Player("Doomedbunnies", None, None),
      Player("Trollikene", None, None),
      Player("Whitenoise", None, None),
      Player("Realofoxtrot", None, None),
      Player("Turnipboy", None, None),
      Player("Sykosloth", None, None),
      Player("Mvem", None, None),
      Player("Paragon12321", None, None),
      Player("Toamini", None, None),
      Player("Gregdebonis", None, None),
      Player("Pressftopayrespect", None, None),
      Player("Rta", None, None),
      Player("Dowsey", None, None),
      Player("Sheph", Some("s76561198018620847/steam"), None),
      Player("thesmiddy", Some("s76561197962409465/steam"), None),
      Player("Samforest", None, None),
      Player("Descolada", None, None),
      Player("Mastrblastr", None, None),
      Player("C9high", None, None),
      Player("Plastikqs", None, None),
      Player("Moon", None, None),
      Player("C00n ", None, None),
      Player("Basshead", None, None),
      Player("Mistajinxy", None, None),
      Player("S76561197998032388", Some("s76561197998032388/steam"), None),
      Player("Incnone", None, None),
      Player("Dbdkmezz", None, None),
      Player("SmonteGaming", Some("s76561197962211538/steam"), None),
      Player("Hjkatz", None, None),
      Player("Exocel", Some("s76561198054993175/steam"), None)))
  )
  val NumberOfTimesEveryonePlaysEachOther = 2

  implicit lazy val Db: DSLContext = DatabaseConfigurator.getDslContext

  private def generatePairs(players: Seq[Player]): List[(Player, Player)] = {
    players.ensuring(_.length % 2 == 0)
    val reversedPlayers = players.reverse
    (for (x <- 0 until players.length / 2)
      yield (players(x), reversedPlayers(x))).toList
  }

  private def rotate(xs: Seq[Player], amount: Int) = xs.drop(amount) ++ xs.take(amount)

  private val ByeText = "bye"

  private def generateWeeklySchedule(players: Seq[Player]) = {
    val playersWithByeIfNeeded = players.size % 2 match {
      case 0 => players
      case 1 => players ++ Seq(Player(ByeText, None, None))
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

  private def generateMatchesOneTime(leagues: Seq[League]) = {
    leagues.map(it => (it, generateWeeklySchedule(it.players)))
  }

  private def generateOneWeek(leagues: Seq[League]) = {
    leagues.map(it => (it, generateWeeklySchedule(it.players).take(1)))
  }

  private def populateWeekMatches(league: League, week: Int, matches: Iterable[(Player, Player)]) = {
    Logger.info(s"populating matches for ${league.name} week $week: ")
    for (curr <- matches) {
      Logger.info(s"${curr._1} vs. ${curr._2}")
    }
  }

  private def persistLeagueData(league: League, matches: Seq[List[(Player, Player)]]) = {
    //step 1: create the league

    val leagueRecord = new DivisionRecord(league.name, league.order, false, 0.toByte, 0.toByte, 0.toByte)
    Db.executeInsert(leagueRecord)

    //step 2: insert the players
    val playerRecords = league.players.map(p => new PlayerRecord(p.name, p.replayName.getOrElse(p.name), league.name, 0, 0, 0, p.country.orNull, 0x01.toByte, 0x01.toByte))
    Db.batchInsert(playerRecords.asJava).execute()

    //step 3: insert all the matches
    for (week <- matches.indices) {
      val thisWeek = matches(week)
      val matchRecords = thisWeek.map(it => new BoutRecord(null, week + 1, league.name, it._1.name, it._2.name, 0, null, null, null, 0, null, null)) // 0 = standard
      Db.batchInsert(matchRecords.asJava).execute()
    }
  }

  private def printLeagueAndMatches(league: League, matches: Seq[List[(Player, Player)]]) = {
    println(s"${league.name} League")
    for (weekNum <- matches.indices) {
      val week = matches(weekNum)
      println(s"\tWeek ${weekNum + 1}")
      val records = week.map(it => s"\t\t${it._1.name} vs ${it._2.name}" )
      println(records.mkString("\n"))
      println()
    }
  }

  private val allLeaguesXMatches = generateMatches(Leagues.dropRight(1)) ++ generateOneWeek(Leagues.takeRight(1))

  for {(league, matches) <- allLeaguesXMatches} {
    printLeagueAndMatches(league, matches)
    persistLeagueData(league, matches)
  }

}
