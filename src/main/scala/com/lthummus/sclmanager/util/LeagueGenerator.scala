package com.lthummus.sclmanager.util


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

  def generatePairs(players: Seq[PlayerName]) = {
    players.ensuring(_.length == 6)
    List((players(0),players(5)),
      (players(1), players(4)),
      (players(2), players(3)))
  }

  private def rotate(xs: Seq[PlayerName], amount: Int) = xs.drop(amount) ++ xs.take(amount)

  def generateWeeklySchedule(players: Seq[PlayerName]) = {
    val anchor = Seq(players.head)
    val everyoneElse = players.tail

    for (x <- 0 until players.length - 1) yield
      generatePairs(anchor ++ rotate(everyoneElse, x))

  }

  def generateMatches(leagues: Seq[League]) = {
    leagues.map(it => (it, generateWeeklySchedule(it.players)))
  }

  def populateWeekMatches(league: League, week: Int, matches: Iterable[(PlayerName, PlayerName)]) = {
    println(s"populating matches for ${league.name} week $week: ")
    for (curr <- matches) {
      println(s"\t${curr._1} vs. ${curr._2}")
    }
  }

  val allLeaguesXMatches = generateMatches(Leagues)


  for {(league, matches) <- allLeaguesXMatches} {
    for (week <- matches.indices) {
      populateWeekMatches(league, week + 1, matches(week))
    }
  }

}
