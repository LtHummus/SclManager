package com.lthummus.sclmanager.util

import com.lthummus.sclmanager.database.DatabaseConfigurator
import com.lthummus.sclmanager.database.dao.{BoutDao, DivisionDao, GameDao, PlayerDao}
import com.lthummus.sclmanager.servlets.dto.Match
import org.jooq.DSLContext


object MatchPrinter extends App {

  implicit val Db: DSLContext = DatabaseConfigurator.getDslContext

  val players = PlayerDao.all()
  val playerMap = players.map(it => (it.getName, it)).toMap
  val divisions = DivisionDao.all().sortBy(_.getPrecedence)
  for (week <- 1 to 10) {
    val bouts = BoutDao.getByWeek(week).map(m => Match.fromDatabaseRecordWithGames(m, GameDao.getGameRecordsByBoutId(m.getId), players.map(it => (it.getName, it)).toMap, None))

    val boutsWithDivision = bouts.groupBy(it => playerMap(it.player1.name).getDivision)

    println(s"[center][b][color=#0000FF][size=150]Week $week[/size][/color][/b][/center]")

    println("[center]")
    for (div <- divisions) {
      boutsWithDivision.get(div.getName) match {
        case Some(boutsWeCareAbout) =>
          println(s"[b]${div.getName}[/b]")
          println(boutsWeCareAbout.map(b => s"${b.player1.name} v. ${b.player2.name}").mkString("\n"))
          println("\n")
        case None => //nop
      }
    }
    println("[/center]")
    println()
    println()

  }


}
