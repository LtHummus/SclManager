package com.lthummus.sclmanager.util

import com.lthummus.sclmanager.database.DatabaseConfigurator
import com.lthummus.sclmanager.database.dao.{BoutDao, DivisionDao, GameDao, PlayerDao}
import com.lthummus.sclmanager.servlets.dto.Match
import org.joda.time.DateTime
import org.jooq.DSLContext
import zzz.generated.Tables


object MatchPrinter extends App {

  def formatWeek(start: DateTime, end: DateTime): String = {
    if (start.monthOfYear().get() == end.monthOfYear().get()) {
      s"${start.monthOfYear().getAsText} ${start.dayOfMonth().get()} - ${end.dayOfMonth().get()}"
    } else {
      s"${start.monthOfYear().getAsText} ${start.dayOfMonth().get()} - ${end.monthOfYear().getAsText} ${end.dayOfMonth().get()}"
    }
  }

  implicit val Db: DSLContext = DatabaseConfigurator.getDslContext

  val firstDayWeekOne = new DateTime(2017, 5, 13, 12, 0)
  val endDayWeekOne = new DateTime(2017, 5, 19, 12, 0)

  val players = PlayerDao.all()
  val playerMap = players.map(it => (it.name, it)).toMap
  val divisions = DivisionDao.all().sortBy(_.getPrecedence)
  for (week <- 1 to 10) {
    val bouts = BoutDao.getByWeek(week).map(m => Match.fromDatabaseRecordWithGames(m, GameDao.getGameRecordsByBoutId(m.into(Tables.BOUT).getId), players.map(it => (it.name, it)).toMap, None))

    val boutsWithDivision = bouts.groupBy(it => playerMap(it.player1.name).divisionName)

    println(s"[center][b][color=#0000FF][size=150]Week $week[/size][/color][/b][/center]")
    println(s"[center][b][color=#0000FF]${formatWeek(firstDayWeekOne.plusWeeks(week - 1), endDayWeekOne.plusWeeks(week - 1))}[/color][/b][/center]")

    println("[center]")
    for (div <- divisions.filter(_.getName == "Challenger")) {
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
