package com.lthummus.sclmanager.util

import com.lthummus.sclmanager.database.DatabaseConfigurator
import com.lthummus.sclmanager.util.LeagueGenerator.Db
import org.jooq.DSLContext
import zzz.generated.tables.records.BoutRecord

import scala.collection.JavaConverters._


object ChallengerRebuild extends App {

  val Matches = List(
    List(("anorexicwhale", "dburke"),
         ("fourliberties", "moon"),
         ("quicklime", "danishspy")),
    List(("moon", "dburke"),
         ("quicklime", "fourliberties"),
         ("danishspy", "anorexicwhale")),
    List(("anorexicwhale", "fourliberties"),
         ("danishspy", "dburke"),
         ("moon", "quicklime")),
    List(("anorexicwhale", "moon"),
         ("danishspy", "fourliberties"),
         ("quicklime", "dburke")),
    List(("anorexicwhale", "quicklime"),
         ("danishspy", "moon"),
         ("fourliberties", "dburke")),

    List(("quicklime", "dburke"),
           ("moon", "danishspy"),
           ("fourliberties", "anorexicwhale")),

    List(("quicklime", "moon"),
           ("fourliberties", "dburke"),
           ("anorexicwhale", "danishspy")),

    List(("quicklime", "fourliberties"),
           ("anorexicwhale", "moon"),
           ("danishspy", "dburke")),

    List(("quicklime", "anorexicwhale"),
           ("danishspy", "fourliberties"),
           ("dburke", "moon")),

    List(("quicklime", "danishspy"),
           ("dburke", "anorexicwhale"),
           ("moon", "fourliberties"))
  )

  implicit val Db: DSLContext = DatabaseConfigurator.getDslContext

  for (week <- Matches.indices) {
    val thisWeek = Matches(week)
    val matchRecords = thisWeek.map(it => new BoutRecord(null, week + 1, "Challenger", it._1, it._2, 0, null, null, null, 0, null, null)) // 0 = standard
    Db.batchInsert(matchRecords.asJava).execute()
  }

}
