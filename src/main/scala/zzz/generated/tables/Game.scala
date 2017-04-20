/**
 * This class is generated by jOOQ
 */
package zzz.generated.tables


import java.lang.Class
import java.lang.Integer
import java.lang.String
import java.sql.Timestamp
import java.util.Arrays
import java.util.List

import javax.annotation.Generated

import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.Identity
import org.jooq.Schema
import org.jooq.Table
import org.jooq.TableField
import org.jooq.UniqueKey
import org.jooq.impl.TableImpl

import scala.Array

import zzz.generated.Keys
import zzz.generated.Scl
import zzz.generated.tables.records.GameRecord


object Game {

  /**
   * The reference instance of <code>scl.game</code>
   */
  val GAME = new Game
}

/**
 * This class is generated by jOOQ.
 */
@Generated(
  value = Array(
    "http://www.jooq.org",
    "jOOQ version:3.8.2"
  ),
  comments = "This class is generated by jOOQ"
)
class Game(alias : String, aliased : Table[GameRecord], parameters : Array[ Field[_] ]) extends TableImpl[GameRecord](alias, Scl.SCL, aliased, parameters, "") {

  /**
   * The class holding records for this type
   */
  override def getRecordType : Class[GameRecord] = {
    classOf[GameRecord]
  }

  /**
   * The column <code>scl.game.id</code>.
   */
  val ID : TableField[GameRecord, Integer] = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), "")

  /**
   * The column <code>scl.game.spy</code>.
   */
  val SPY : TableField[GameRecord, String] = createField("spy", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.VARCHAR)), "")

  /**
   * The column <code>scl.game.sniper</code>.
   */
  val SNIPER : TableField[GameRecord, String] = createField("sniper", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.VARCHAR)), "")

  /**
   * The column <code>scl.game.bout</code>.
   */
  val BOUT : TableField[GameRecord, Integer] = createField("bout", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), "")

  /**
   * The column <code>scl.game.result</code>.
   */
  val RESULT : TableField[GameRecord, Integer] = createField("result", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), "")

  /**
   * The column <code>scl.game.sequence</code>.
   */
  val SEQUENCE : TableField[GameRecord, Integer] = createField("sequence", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), "")

  /**
   * The column <code>scl.game.venue</code>.
   */
  val VENUE : TableField[GameRecord, String] = createField("venue", org.jooq.impl.SQLDataType.CLOB.nullable(false), "")

  /**
   * The column <code>scl.game.gametype</code>.
   */
  val GAMETYPE : TableField[GameRecord, String] = createField("gametype", org.jooq.impl.SQLDataType.CLOB.nullable(false), "")

  /**
   * The column <code>scl.game.uuid</code>.
   */
  val UUID : TableField[GameRecord, String] = createField("uuid", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false), "")

  /**
   * The column <code>scl.game.timestamp</code>.
   */
  val TIMESTAMP : TableField[GameRecord, Timestamp] = createField("timestamp", org.jooq.impl.SQLDataType.TIMESTAMP, "")

  /**
   * Create a <code>scl.game</code> table reference
   */
  def this() = {
    this("game", null, null)
  }

  /**
   * Create an aliased <code>scl.game</code> table reference
   */
  def this(alias : String) = {
    this(alias, zzz.generated.tables.Game.GAME, null)
  }

  private def this(alias : String, aliased : Table[GameRecord]) = {
    this(alias, aliased, null)
  }

  override def getSchema : Schema = Scl.SCL

  override def getIdentity : Identity[GameRecord, Integer] = {
    Keys.IDENTITY_GAME
  }

  override def getPrimaryKey : UniqueKey[GameRecord] = {
    Keys.KEY_GAME_PRIMARY
  }

  override def getKeys : List[ UniqueKey[GameRecord] ] = {
    return Arrays.asList[ UniqueKey[GameRecord] ](Keys.KEY_GAME_PRIMARY, Keys.KEY_GAME_IDX_UUID)
  }

  override def getReferences : List[ ForeignKey[GameRecord, _] ] = {
    return Arrays.asList[ ForeignKey[GameRecord, _] ](Keys.FK_SPY, Keys.FK_SNIPER, Keys.FK_MATCH)
  }

  override def as(alias : String) : Game = {
    new Game(alias, this)
  }

  /**
   * Rename this table
   */
  def rename(name : String) : Game = {
    new Game(name, null)
  }
}
