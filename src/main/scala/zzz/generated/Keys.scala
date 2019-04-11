/*
 * This file is generated by jOOQ.
 */
package zzz.generated


import java.lang.Integer

import javax.annotation.Generated

import org.jooq.ForeignKey
import org.jooq.Identity
import org.jooq.UniqueKey
import org.jooq.impl.Internal

import scala.Array

import zzz.generated.tables.Bout
import zzz.generated.tables.Division
import zzz.generated.tables.Draft
import zzz.generated.tables.Game
import zzz.generated.tables.Player
import zzz.generated.tables.records.BoutRecord
import zzz.generated.tables.records.DivisionRecord
import zzz.generated.tables.records.DraftRecord
import zzz.generated.tables.records.GameRecord
import zzz.generated.tables.records.PlayerRecord


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>scl</code> schema.
 */
@Generated(
  value = Array(
    "http://www.jooq.org",
    "jOOQ version:3.11.11"
  ),
  comments = "This class is generated by jOOQ"
)
object Keys {

  // -------------------------------------------------------------------------
  // IDENTITY definitions
  // -------------------------------------------------------------------------

  val IDENTITY_BOUT = Identities0.IDENTITY_BOUT
  val IDENTITY_DRAFT = Identities0.IDENTITY_DRAFT
  val IDENTITY_GAME = Identities0.IDENTITY_GAME

  // -------------------------------------------------------------------------
  // UNIQUE and PRIMARY KEY definitions
  // -------------------------------------------------------------------------

  val KEY_BOUT_PRIMARY = UniqueKeys0.KEY_BOUT_PRIMARY
  val KEY_DIVISION_PRIMARY = UniqueKeys0.KEY_DIVISION_PRIMARY
  val KEY_DRAFT_PRIMARY = UniqueKeys0.KEY_DRAFT_PRIMARY
  val KEY_GAME_PRIMARY = UniqueKeys0.KEY_GAME_PRIMARY
  val KEY_GAME_IDX_UUID = UniqueKeys0.KEY_GAME_IDX_UUID
  val KEY_PLAYER_PRIMARY = UniqueKeys0.KEY_PLAYER_PRIMARY

  // -------------------------------------------------------------------------
  // FOREIGN KEY definitions
  // -------------------------------------------------------------------------

  val FK_DIVISON = ForeignKeys0.FK_DIVISON
  val FK_PLAYER1 = ForeignKeys0.FK_PLAYER1
  val FK_PLAYER2 = ForeignKeys0.FK_PLAYER2
  val FK_WINNER = ForeignKeys0.FK_WINNER
  val FK_DRAFT = ForeignKeys0.FK_DRAFT
  val FK_SPY = ForeignKeys0.FK_SPY
  val FK_SNIPER = ForeignKeys0.FK_SNIPER
  val FK_MATCH = ForeignKeys0.FK_MATCH
  val FK_LEAGUE = ForeignKeys0.FK_LEAGUE

  // -------------------------------------------------------------------------
  // [#1459] distribute members to avoid static initialisers > 64kb
  // -------------------------------------------------------------------------

  private object Identities0 {
    val IDENTITY_BOUT : Identity[BoutRecord, Integer] = Internal.createIdentity(Bout.BOUT, Bout.BOUT.ID)
    val IDENTITY_DRAFT : Identity[DraftRecord, Integer] = Internal.createIdentity(Draft.DRAFT, Draft.DRAFT.ID)
    val IDENTITY_GAME : Identity[GameRecord, Integer] = Internal.createIdentity(Game.GAME, Game.GAME.ID)
  }

  private object UniqueKeys0 {
    val KEY_BOUT_PRIMARY : UniqueKey[BoutRecord] = Internal.createUniqueKey(Bout.BOUT, "KEY_bout_PRIMARY", Bout.BOUT.ID)
    val KEY_DIVISION_PRIMARY : UniqueKey[DivisionRecord] = Internal.createUniqueKey(Division.DIVISION, "KEY_division_PRIMARY", Division.DIVISION.NAME)
    val KEY_DRAFT_PRIMARY : UniqueKey[DraftRecord] = Internal.createUniqueKey(Draft.DRAFT, "KEY_draft_PRIMARY", Draft.DRAFT.ID)
    val KEY_GAME_PRIMARY : UniqueKey[GameRecord] = Internal.createUniqueKey(Game.GAME, "KEY_game_PRIMARY", Game.GAME.ID)
    val KEY_GAME_IDX_UUID : UniqueKey[GameRecord] = Internal.createUniqueKey(Game.GAME, "KEY_game_idx_uuid", Game.GAME.UUID)
    val KEY_PLAYER_PRIMARY : UniqueKey[PlayerRecord] = Internal.createUniqueKey(Player.PLAYER, "KEY_player_PRIMARY", Player.PLAYER.NAME)
  }

  private object ForeignKeys0 {
    val FK_DIVISON : ForeignKey[BoutRecord, DivisionRecord] = Internal.createForeignKey(zzz.generated.Keys.KEY_DIVISION_PRIMARY, Bout.BOUT, "fk_divison", Bout.BOUT.DIVISION)
    val FK_PLAYER1 : ForeignKey[BoutRecord, PlayerRecord] = Internal.createForeignKey(zzz.generated.Keys.KEY_PLAYER_PRIMARY, Bout.BOUT, "fk_player1", Bout.BOUT.PLAYER1)
    val FK_PLAYER2 : ForeignKey[BoutRecord, PlayerRecord] = Internal.createForeignKey(zzz.generated.Keys.KEY_PLAYER_PRIMARY, Bout.BOUT, "fk_player2", Bout.BOUT.PLAYER2)
    val FK_WINNER : ForeignKey[BoutRecord, PlayerRecord] = Internal.createForeignKey(zzz.generated.Keys.KEY_PLAYER_PRIMARY, Bout.BOUT, "fk_winner", Bout.BOUT.FORFEIT_WINNER)
    val FK_DRAFT : ForeignKey[BoutRecord, DraftRecord] = Internal.createForeignKey(zzz.generated.Keys.KEY_DRAFT_PRIMARY, Bout.BOUT, "fk_draft", Bout.BOUT.DRAFT)
    val FK_SPY : ForeignKey[GameRecord, PlayerRecord] = Internal.createForeignKey(zzz.generated.Keys.KEY_PLAYER_PRIMARY, Game.GAME, "fk_spy", Game.GAME.SPY)
    val FK_SNIPER : ForeignKey[GameRecord, PlayerRecord] = Internal.createForeignKey(zzz.generated.Keys.KEY_PLAYER_PRIMARY, Game.GAME, "fk_sniper", Game.GAME.SNIPER)
    val FK_MATCH : ForeignKey[GameRecord, BoutRecord] = Internal.createForeignKey(zzz.generated.Keys.KEY_BOUT_PRIMARY, Game.GAME, "fk_match", Game.GAME.BOUT)
    val FK_LEAGUE : ForeignKey[PlayerRecord, DivisionRecord] = Internal.createForeignKey(zzz.generated.Keys.KEY_DIVISION_PRIMARY, Player.PLAYER, "fk_league", Player.PLAYER.DIVISION)
  }
}
