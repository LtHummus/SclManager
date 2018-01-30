package com.lthummus.sclmanager.util

import com.lthummus.sclmanager.database.DatabaseConfigurator
import com.lthummus.sclmanager.database.dao.{DraftDao, GameDao}
import com.lthummus.sclmanager.parsing.GameResultEnum
import com.lthummus.sclmanager.servlets.dto.Game
import com.typesafe.config.ConfigFactory
import org.jooq.DSLContext
import org.json4s._
import org.json4s.jackson.Serialization.read

object StatCollector extends App {

  case class DraftData(bannedMaps: List[DraftSelection], pickedMaps: List[DraftSelection])
  case class DraftSelection(picker: String, map: String)

  implicit val dslContext: DSLContext = DatabaseConfigurator.getDslContext
  implicit val formats = DefaultFormats

  val SpyWin = Set(GameResultEnum.MissionWin, GameResultEnum.CivilianShot).map(_.internalId)
  val SniperWin = Set(GameResultEnum.SpyShot, GameResultEnum.SpyTimeout).map(_.internalId)

  private def printDraftData(username: String): Unit = {
    val drafts = DraftDao.all().filter(x => DraftDao.isDraftUsed(x.getId)).filter(x =>
      x.getPlayer1 == username || x.getPlayer2 == username
    ).map(x => read[DraftData](x.getPayload))

    val ourBans = drafts.flatMap(_.bannedMaps.filter(_.picker == username))
    val ourPicks = drafts.flatMap(_.pickedMaps.filter(_.picker == username))

    val orderedBans = ourBans.groupBy(_.map).mapValues(_.size).toList.sortBy(_._2).reverse.map(x => s"${x._1} banned ${x._2} times").mkString("\n")
    val orderedPicks = ourPicks.groupBy(_.map).mapValues(_.size).toList.sortBy(_._2).reverse.map(x => s"${x._1} picked ${x._2} times").mkString("\n")

    println("Most banned:")
    println(orderedBans)
    println()
    println("Most picked:")
    println(orderedPicks)
    println()
  }

  private def printData(username: String): Unit = {
    println(s"Stats for $username")
    printDraftData(username)
    printOverallStats(username)
    println()
    println()
    println()

  }

  private def printOverallStats(username: String): Unit = {
    val allGames = GameDao.all

    val spyGames = allGames.filter(_.getSpy == username)
    val sniperGames = allGames.filter(_.getSniper == username)

    val spyWins = spyGames.count(x => SpyWin.contains(x.getResult))
    val sniperWins = sniperGames.count(x => SniperWin.contains(x.getResult))

    val spyLosses = spyGames.length - spyWins
    val sniperLosses = sniperGames.length - sniperWins

    println(s"Spy record: $spyWins-$spyLosses")
    println(s"Sniper record: $sniperWins-$sniperLosses")
  }

  GameDao.all

  printData("krazycaley")
  printData("kcmmmmm")
}
