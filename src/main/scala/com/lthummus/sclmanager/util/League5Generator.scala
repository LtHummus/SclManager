package com.lthummus.sclmanager.util

import com.lthummus.sclmanager.database.DatabaseConfigurator
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import zzz.generated.tables.records.{BoutRecord, DivisionRecord, PlayerRecord}

import scala.collection.JavaConverters._


private object League5Generator extends App {
  val Logger = LoggerFactory.getLogger(getClass)

  private case class Player(var name: String, var replayName: Option[String], country: Option[String]) {
    name = name.toLowerCase
    replayName = replayName.map(_.toLowerCase)
  }
  private case class League(order: Int, name: String, rounds: Int, players: Seq[Player])
  private case class Match(player1: Player, player2: Player)

  private val Leagues = Seq(
    League(0, "Diamond", 2, Seq(
      Player("KrazyCaley", None, None),
      Player("CanadianBacon", None, None),
      Player("WarningTrack", None, None),
      Player("KCMmmmm", None, None),
      Player("Cleetose", None, None),
      Player("Magician1099", None, None)
    )),
    League(1, "Platinum", 2, Seq(
      Player("Slappydavis", None, None),
      Player("Pwndnoob", None, None),
      Player("Falconhit", None, None),
      Player("Scallions", None, None),
      Player("Bananaconda", None, None),
      Player("Yerand", None, None)
    )),
    League(2, "Gold", 2, Seq(
      Player("Wodar", None, None),
      Player("Royalflush", None, None),
      Player("Turnout8", None, None),
      Player("Yeesh", None, None),
      Player("Lazybear", None, None),
      Player("OpiWrites", Some("s76561198072104672/steam"), None)
    )),
    League(3, "Silver", 1, Seq(
      Player("Checker", None, None),
      Player("Jyaty", None, None),
      Player("Arturiax", None, None),
      Player("Sgnurf", None, None),
      Player("Baldrick", None, None),
      Player("Gmantsang", None, None),
      Player("Ml726", None, None),
      Player("Dowsey", None, None),
      Player("Skrewwl00se", None, None),
      Player("Cartwright", Some("s76561197975491705/steam"), None),
      Player("Watermeat", Some("s76561198027742748/steam"), None),
      Player("Pofke", None, None),
      Player("Mintyrug", None, None)
    )),
    League(4, "Bronze", 1, Seq(
      Player("Pox", None, None),
      Player("Turnipboy", None, None),
      Player("Mrrgrs", None, None),
      Player("Davidw", None, None),
      Player("Brskaylor", None, None),
      Player("Degran", None, None),
      Player("Amlabella", None, None),
      Player("Sheph", Some("s76561198018620847/steam"), None),
      Player("Bitbandingpig", None, None),
      Player("Hectic", None, None),
      Player("FurbyFubar", None, None),
      Player("Ryooo", Some("s76561197968066722/steam"), None),
      Player("Hunu", None, None)
    )),
    League(5, "Copper", 1, Seq(
      Player("Max Edward Snax", Some("s76561198059936621/steam"), None),
      Player("Soolseem", None, None),
      Player("Frostie", Some("s76561198151928676/steam"), None),
      Player("Alexare", Some("s76561197993456144/steam"), None),
      Player("Zerodoom", None, None),
      Player("Quicklime", None, None),
      Player("HumanKirby", None, None),
      Player("Tristram", None, None),
      Player("Belial", None, None),
      Player("Iggythegrifter", None, None),
      Player("Kmars133", Some("s76561198362472284/steam"), None),
      Player("Calvin Schoolidge", Some("s76561197991918374/steam"), None),
      Player("NinjaFairy", None, None)
    )),
    League(6, "Iron", 1, Seq(
      Player("Dbdkmezz", None, None),
      Player("Magicdoer1", None, None),
      Player("Portalfreek", None, None),
      Player("Rta", None, None),
      Player("Dukit", None, None),
      Player("Fancypants", None, None),
      Player("TheSmiddy", Some("s76561197962409465/steam"), None),
      Player("The_Usual_Toaster", Some("s76561198315491931/steam"), None),
      Player("Howiie", None, None),
      Player("Gabrio", Some("s76561198376870266/steam"), None),
      Player("Kotte", Some("s76561198062302050/steam"), None),
      Player("Orac", None, None),
      Player("Ranmilia", Some("s76561197995259588/steam"), None)
    )),
    League(7, "Obsidian", 1, Seq(
      Player("Kevino", None, None),
      Player("PixelBandit", Some("s76561198276393582/steam"), None),
      Player("Silverthorn", Some("s76561198121407701/steam"), None),
      Player("Jd105L", None, None),
      Player("Alteffor", None, None),
      Player("Sergioc89", None, None),
      Player("Tflameee", Some("s76561198245290402/steam"), None),
      Player("AndiVX", Some("s76561198025378584/steam"), None),
      Player("Ascendbeyond", None, None),
      Player("GASOL", Some("s76561197967390667/steam"), None),
      Player("Daheadhunter", None, None),
      Player("Juliusb", None, None),
      Player("Vlady", Some("s76561197995295413/steam"), None)
    )),
    League(8, "Oak", 1, Seq(
      Player("Steel-Ph3no", Some("s76561197995259588/steam"), None),
      Player("MosbyArchitect", Some("s76561198064312826/steam"), None),
      Player("Tortuga-Man", Some("s76561197994122963/steam"), None),
      Player("NeRagEk", Some("s76561198865665163/steam"), None),
      Player("STORM", None, None),
      Player("FreezeTVSK", Some("s76561198865324334/steam"), None),
      Player("Fuffle Butts", Some("s76561198117282143/steam"), None),
      Player("ThatOdinaryPlayer", Some("s76561198208045756/steam"), None),
      Player("Legorve Genine", Some("s76561198089868938/steam"), None),
      Player("Dkaka", Some("s76561198186198298/steam"), None),
      Player("Libro", Some("s76561198213211957/steam"), None),
      Player("Ok!!!", None, None),
      Player("Harren", Some("s76561198039520355/steam"), None)
    )),
    League(9, "Bamboo", 1, Seq(
      Player("hi", None, None),
      Player("Mazn", Some("s76561197972679355/steam"), None),
      Player("/steam", Some("s76561198009956577/steam"), None),
      Player("EnzyIV", None, None),
      Player("Phimegan", Some("s76561198253374033/steam"), None),
      Player("Bananakaya", Some("s76561198210693144/steam"), None),
      Player("Mati", Some("s76561198046901916/steam"), None),
      Player("C_line", Some("s76561198038751433/steam"), None),
      Player("Fluffytragedies", None, None),
      Player("Azu", None, None),
      Player("Konglejonni5", Some("s76561198197625387/steam"), None),
      Player("InfamousCupcake", Some("s76561198097862978/steam"), None)
    ))
  )

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
    leagues.map(it =>
      if (it.rounds == 2) {
        (it, generateWeeklySchedule(it.players) ++ generateWeeklySchedule(it.players))
      } else {
        (it, generateWeeklySchedule(it.players))
      })
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
    val playerRecords = league.players.map(p => new PlayerRecord(p.name.toLowerCase, p.replayName.getOrElse(p.name.toLowerCase), league.name, 0, 0, 0, p.country.orNull, 0x01.toByte, 0x01.toByte))
    Db.batchInsert(playerRecords.asJava).execute()

    //step 3: insert all the matches
    for (week <- matches.indices) {
      val thisWeek = matches(week)
      val matchRecords = thisWeek.map(it => new BoutRecord(null, week + 1, league.name, it._1.name.toLowerCase, it._2.name.toLowerCase, 0, null, null, null, 0, null, null)) // 0 = standard
      Db.batchInsert(matchRecords.asJava).execute()
    }
  }

  private def printLeagueAndMatches(league: League, matches: Seq[List[(Player, Player)]]) = {
    println(s"${league.name} League")
    for (weekNum <- matches.indices) {
      val week = matches(weekNum)
      println(s"\tWeek ${weekNum + 1}")
      val records = week.map(it => s"\t\t${it._1.name.toLowerCase} vs ${it._2.name.toLowerCase}" )
      println(records.mkString("\n"))
      println()
    }
  }

  private val allLeaguesXMatches = generateMatches(Leagues)

  for {(league, matches) <- allLeaguesXMatches} {
    printLeagueAndMatches(league, matches)
    persistLeagueData(league, matches)
  }
}
