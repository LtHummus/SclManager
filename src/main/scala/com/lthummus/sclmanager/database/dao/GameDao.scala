package com.lthummus.sclmanager.database.dao

import com.lthummus.sclmanager.database.data.Game
import com.lthummus.sclmanager.parsing.Replay
import org.jooq.DSLContext
import zzz.generated.Tables

import scala.collection.JavaConverters._
import scalaz._
import Scalaz._

object GameDao {

  def getGamesByMatchId(matchId: Int, nameDecoder: PartialFunction[Int, String])(implicit dslContext: DSLContext): String \/ List[Replay] = {
    val res = dslContext
      .selectFrom(Tables.GAME)
      .where(Tables.GAME.MATCH.eq(matchId))
      .orderBy(Tables.GAME.SEQUENCE)
      .fetch()

    val replays = res.asScala.map(it => Game.toReplay(it, nameDecoder(it.getSpy), nameDecoder(it.getSniper))).collect{ case \/-(it) => it}.toList

    if (res.size() == replays.size)
      replays.right
    else
      "Could not decode all replays".left

  }
}
