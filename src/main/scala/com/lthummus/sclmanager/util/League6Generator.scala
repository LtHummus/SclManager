package com.lthummus.sclmanager.util

import java.util.Scanner

import com.lthummus.sclmanager.database.DatabaseConfigurator
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import zzz.generated.tables.records.{BoutRecord, DivisionRecord, PlayerRecord}

import scala.collection.JavaConverters._


private object League6Generator extends App {
  val Logger = LoggerFactory.getLogger(getClass)

  private case class Player(var name: String, var replayName: Option[String], country: Option[String]) {
    name = name.toLowerCase
    replayName = replayName.map(_.toLowerCase)
  }
  private case class League(order: Int, name: String, rounds: Int, players: Seq[Player])
  private case class Match(player1: Player, player2: Player)

  private val Leagues = Seq(
    League(0, "Diamond", 2, Seq(
      Player("warningtrack", None, None),
      Player("pwndnoob", None, None),
      Player("yerand", None, None),
      Player("falconhit", None, None),
      Player("slappydavis", None, None),
      Player("magician1099", None, None)
    )),
    League(1, "Platinum", 2, Seq(
      Player("yeesh", None, None),
      Player("opiwrites", Some("s76561198072104672/steam"), None),
      Player("lazybear", None, None),
      Player("pofke", None, None),
      Player("ryooo", Some("s76561197968066722/steam"), None),
      Player("skrewwl00se", None, None)
    )),
    League(2, "Gold", 2, Seq(
      Player("cameraman", None, None),
      Player("turnout8", None, None),
      Player("wodar", None, None),
      Player("royalflush", None, None),
      Player("sheph", Some("s76561198018620847/steam"), None),
      Player("kotte", Some("s76561198062302050/steam"), None)
    )),
    League(3, "Silver", 1, Seq(
      Player("checker", None, None),
      Player("nanthelas", None, None),
      Player("lauras43", Some("s76561198109490855/steam"), None),
      Player("beanie", None, None),
      Player("kumakid", Some("s76561198005706410/steam"), None),
      Player("spedmonkey", None, None),
      Player("miniorek", None, None),
      Player("turnipboy", None, None),
      Player("davidw", None, None),
      Player("hunu", None, None),
      Player("furbyfubar", None, None),
      Player("sgnurf", None, None),
      Player("zerodoom", None, None)
    )),
    League(4, "Bronze", 1, Seq(
      Player("sykosloth", None, None),
      Player("tonyl", Some("s76561198229598676/steam"), None),
      Player("iggythegrifter", None, None),
      Player("calvinschoolidge", None, None),
      Player("humankirby", None, None),
      Player("kmars133", Some("s76561198095713921/steam"), None),
      Player("quicklime", None, None),
      Player("mrrgrs", None, None),
      Player("dukit", None, None),
      Player("magicdoer1", None, None),
      Player("dbdkmezz", None, None),
      Player("portallfreek", None, None),
      Player("gabrio", Some("s76561198376870266/steam"), None)
    )),
    League(5, "Copper", 1, Seq(
      Player("bananabread", Some("s76561198054557346/steam"), None),
      Player("monopolyman", None, None),
      Player("howiie", None, None),
      Player("alexare", Some("s76561197993456144/steam"), None),
      Player("jd105l", None, None),
      Player("tflameee", Some("s76561198245290402/steam"), None),
      Player("andivx", Some("s76561198025378584/steam"), None),
      Player("legorvegenine", Some("s76561198089868938/steam"), None),
      Player("monaters", None, None),
      Player("yglini", Some("s76561198028132210/steam"), None),
      Player("harren", Some("s76561198039520355/steam"), None),
      Player("paratroopa", None, None),
      Player("conpope", Some("s76561198253374033/steam"), None)
    )),
    League(6, "Iron", 1, Seq(
      Player("mrtwister", None, None),
      Player("the_usual_toaster", None, None),
      Player("sunbro", Some("s76561198010327487/steam"), None),
      Player("vac58", Some("s76561198398392401/steam"), None),
      Player("catnip", None, None),
      Player("levtrotskij", Some("s76561198139681334/steam"), None),
      Player("rov", Some("s76561198318762388/steam"), None),
      Player("beaniesteam", Some("s76561197995727130/steam"), None),
      Player("dejoker", None, None),
      Player("ytterbijum", None, None),
      Player("jahni", Some("s76561197971768284/steam"), None),
      Player("rhythmicsheep", Some("s76561198072890132/steam"), None),
      Player("koopa", Some("s76561198831660786/steam"), None)
    )),
    League(7, "Obsidian", 1, Seq(
      Player("auglbe240", Some("s76561198873640845/steam"), None),
      Player("bananasniper", None, None),
      Player("theforgot3n1", Some("s76561198046058276/steam"), None),
      Player("pipesuper24", None, None),
      Player("corvusmellori", Some("s76561198037680966/steam"), None),
      Player("xeynoxys", Some("s76561198055372649/steam"), None),
      Player("scout", None, None),
      Player("monsterracer", Some("s76561198317639601/steam"), None),
      Player("altinsider", Some("s76561198000520176/steam"), None),
      Player("shootinggoats", Some("s76561198093587299/steam"), None),
      Player("lushmoss", Some("s76561198003483457/steam"), None),
      Player("bobthealmighty", Some("s76561198063452950/steam"), None),
      Player("deathtacticus", Some("s76561198018973206/steam"), None)
    )),
    League(8, "Oak", 1, Seq(
      Player("phillammon", Some("s76561198022204052/steam"), None),
      Player("tonewyork", Some("s76561198798955853/steam"), None),
      Player("testierjamaj", Some("s76561198057426878/steam"), None),
      Player("atia", Some("s76561198047142068/steam"), None),
      Player("maxstermind", None, None),
      Player("cptbasch", None, None),
      Player("smashblade", None, None),
      Player("armageddon", Some("s76561199052506574/steam"), None),
      Player("ibutra", None, None),
      Player("igod", None, None)
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

  private def betweenPlayers(p1: String, p2: String)(bout: (Player, Player)): Boolean = {
    p1 == bout._1.name && p2 == bout._2.name || p1 == bout._2.name && p2 == bout._1.name
  }

  private def generateWeeklySchedule(players: Seq[Player]): IndexedSeq[List[(Player, Player)]] = {
    var ret: IndexedSeq[List[(Player, Player)]] = IndexedSeq()
    var ok = false
    val playersWithByeIfNeeded = players.size % 2 match {
      case 0 => players
      case 1 => players ++ Seq(Player(ByeText, None, None))
    }

    val start = System.currentTimeMillis()
    var total = 0
    playersWithByeIfNeeded.permutations.foreach{ shuffledPlayers =>
      val shuffledPlayers = scala.util.Random.shuffle(playersWithByeIfNeeded)

      val anchor = Seq(shuffledPlayers.head)
      val everyoneElse = shuffledPlayers.tail

      val pairs = for (x <- 0 until shuffledPlayers.length - 1) yield
        generatePairs(anchor ++ rotate(everyoneElse, x))

      ret = pairs.map(it => it.filterNot(containsBye))

      val match1Ok = ret.head.exists{betweenPlayers("tonewyork", "testierjamaj")}
      val match2Ok = ret.head.exists{betweenPlayers("cptbasch", "phillammon")}
      val match3Ok = ret.head.exists{betweenPlayers("maxstermind", "atia")}
      val match4Ok = ret.head.exists{betweenPlayers("armageddon", "smashblade")}
      if (match1Ok && match2Ok && match3Ok && match4Ok) {
        total += 1
      }
    }

    val duration = System.currentTimeMillis() - start

    println(s"${duration}ms")
    println(total)

    //for (shuffledPlayers <- playersWithByeIfNeeded.permutations) {
    while (!ok) {
      val shuffledPlayers = scala.util.Random.shuffle(playersWithByeIfNeeded)

      val anchor = Seq(shuffledPlayers.head)
      val everyoneElse = shuffledPlayers.tail

      val pairs = for (x <- 0 until shuffledPlayers.length - 1) yield
        generatePairs(anchor ++ rotate(everyoneElse, x))

      ret = pairs.map(it => it.filterNot(containsBye))

      val match1Ok = ret.head.exists{betweenPlayers("tonewyork", "testierjamaj")}
      val match2Ok = ret.head.exists{betweenPlayers("cptbasch", "phillammon")}
      val match3Ok = ret.head.exists{betweenPlayers("maxstermind", "atia")}
      val match4Ok = ret.head.exists{betweenPlayers("armageddon", "smashblade")}
      ok = match1Ok && match2Ok && match3Ok && match4Ok
//      if (ok) {
//        println("done")
//      }
    }

    println("finished")
    ret

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
    //Db.executeInsert(leagueRecord)

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

  private val allLeaguesXMatches = generateMatches(Leagues.filter(_.name == "Oak"))

  for {(league, matches) <- allLeaguesXMatches} {
    printLeagueAndMatches(league, matches)
    val scan = new Scanner(System.in)
    println("go?")
    scan.nextLine()
    println("going")
    persistLeagueData(league, matches)
  }
}
