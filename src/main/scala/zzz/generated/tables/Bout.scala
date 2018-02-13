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
import zzz.generated.tables.records.BoutRecord


object Bout {

  /**
   * The reference instance of <code>scl.bout</code>
   */
  val BOUT = new Bout
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
class Bout(alias : String, aliased : Table[BoutRecord], parameters : Array[ Field[_] ]) extends TableImpl[BoutRecord](alias, Scl.SCL, aliased, parameters, "") {

  /**
   * The class holding records for this type
   */
  override def getRecordType : Class[BoutRecord] = {
    classOf[BoutRecord]
  }

  /**
   * The column <code>scl.bout.id</code>.
   */
  val ID : TableField[BoutRecord, Integer] = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), "")

  /**
   * The column <code>scl.bout.week</code>.
   */
  val WEEK : TableField[BoutRecord, Integer] = createField("week", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), "")

  /**
   * The column <code>scl.bout.division</code>.
   */
  val DIVISION : TableField[BoutRecord, String] = createField("division", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.VARCHAR)), "")

  /**
   * The column <code>scl.bout.player1</code>.
   */
  val PLAYER1 : TableField[BoutRecord, String] = createField("player1", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.VARCHAR)), "")

  /**
   * The column <code>scl.bout.player2</code>.
   */
  val PLAYER2 : TableField[BoutRecord, String] = createField("player2", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.VARCHAR)), "")

  /**
   * The column <code>scl.bout.status</code>.
   */
  val STATUS : TableField[BoutRecord, Integer] = createField("status", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), "")

  /**
   * The column <code>scl.bout.forfeit_winner</code>.
   */
  val FORFEIT_WINNER : TableField[BoutRecord, String] = createField("forfeit_winner", org.jooq.impl.SQLDataType.VARCHAR.length(50).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.VARCHAR)), "")

  /**
   * The column <code>scl.bout.match_url</code>.
   */
  val MATCH_URL : TableField[BoutRecord, String] = createField("match_url", org.jooq.impl.SQLDataType.CLOB, "")

  /**
   * The column <code>scl.bout.draft</code>.
   */
  val DRAFT : TableField[BoutRecord, Integer] = createField("draft", org.jooq.impl.SQLDataType.INTEGER, "")

  /**
   * The column <code>scl.bout.bout_type</code>.
   */
  val BOUT_TYPE : TableField[BoutRecord, Integer] = createField("bout_type", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), "")

  /**
   * The column <code>scl.bout.timestamp</code>.
   */
  val TIMESTAMP : TableField[BoutRecord, Timestamp] = createField("timestamp", org.jooq.impl.SQLDataType.TIMESTAMP, "")

  /**
   * The column <code>scl.bout.forfeit_text</code>.
   */
  val FORFEIT_TEXT : TableField[BoutRecord, String] = createField("forfeit_text", org.jooq.impl.SQLDataType.VARCHAR.length(200).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.VARCHAR)), "")

  /**
   * Create a <code>scl.bout</code> table reference
   */
  def this() = {
    this("bout", null, null)
  }

  /**
   * Create an aliased <code>scl.bout</code> table reference
   */
  def this(alias : String) = {
    this(alias, zzz.generated.tables.Bout.BOUT, null)
  }

  private def this(alias : String, aliased : Table[BoutRecord]) = {
    this(alias, aliased, null)
  }

  override def getSchema : Schema = Scl.SCL

  override def getIdentity : Identity[BoutRecord, Integer] = {
    Keys.IDENTITY_BOUT
  }

  override def getPrimaryKey : UniqueKey[BoutRecord] = {
    Keys.KEY_BOUT_PRIMARY
  }

  override def getKeys : List[ UniqueKey[BoutRecord] ] = {
    return Arrays.asList[ UniqueKey[BoutRecord] ](Keys.KEY_BOUT_PRIMARY)
  }

  override def getReferences : List[ ForeignKey[BoutRecord, _] ] = {
    return Arrays.asList[ ForeignKey[BoutRecord, _] ](Keys.FK_DIVISON, Keys.FK_PLAYER1, Keys.FK_PLAYER2, Keys.FK_WINNER, Keys.FK_DRAFT)
  }

  override def as(alias : String) : Bout = {
    new Bout(alias, this)
  }

  /**
   * Rename this table
   */
  def rename(name : String) : Bout = {
    new Bout(name, null)
  }
}
