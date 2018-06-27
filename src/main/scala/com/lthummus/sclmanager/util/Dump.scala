package com.lthummus.sclmanager.util


import java.io.PrintWriter

import com.lthummus.sclmanager.database.DatabaseConfigurator
import com.lthummus.sclmanager.database.dao.{BoutDao, DivisionDao, GameDao, PlayerDao}
import com.lthummus.sclmanager.servlets.dto.{Game, Match, Player}
import org.json4s.jackson.Serialization.{read, write}
import org.jooq.DSLContext
import org.json4s.{DefaultFormats, FieldSerializer, Formats}
import org.json4s.ext.JodaTimeSerializers
import org.scalatra.Ok

object Dump extends App {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats ++ JodaTimeSerializers.all + FieldSerializer[Game]()
  implicit val dslContext: DSLContext = DatabaseConfigurator.getDslContext

  val players = PlayerDao.all()
  val everything = BoutDao.getAll().map(Match.fromDatabaseRecordWithGames(_, List(), players.map(it => (it.getName, it)).toMap, None))
  val ids = everything.map(_.id)

  val records = ids.map(curr => {
    val res = BoutDao.getFullBoutRecords(curr)
    val thing = res.getOrElse(throw new IllegalArgumentException())
    Match.fromDatabaseRecordWithGames(thing.bout, thing.games, thing.playerMap, thing.draft)
  })

  //Match.fromDatabaseRecordWithGames(it.bout, it.games, it.playerMap, it.draft)
  println(write(records))
//
//  val parsedPlayers = players.map(player => {
//    val leaguePlayers = DivisionDao.getPlayersInDivision(player.getDivision)
//    val playerMap = leaguePlayers.map(it => (it.getName, it)).toMap
//    val matchRecords = BoutDao.getMatchesForPlayer(player.getName)
//    val matches = matchRecords.map(m => Match.fromDatabaseRecordWithGames(m, GameDao.getGameRecordsByBoutId(m.getId), playerMap, None))
//
//    Player.fromDatabaseRecord(player, Some(matches))
//  })
//
//  val fileString = write(parsedPlayers)
//
//  new PrintWriter("scl3_players.json") {
//    write(fileString)
//    close
//  }



}
