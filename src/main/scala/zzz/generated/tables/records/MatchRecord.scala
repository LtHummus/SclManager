/**
 * This class is generated by jOOQ
 */
package zzz.generated.tables.records


import java.lang.Integer

import javax.annotation.Generated

import org.jooq.Field
import org.jooq.Record1
import org.jooq.Record6
import org.jooq.Row6
import org.jooq.impl.UpdatableRecordImpl

import scala.Array

import zzz.generated.tables.Match


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
class MatchRecord extends UpdatableRecordImpl[MatchRecord](Match.MATCH) with Record6[Integer, Integer, Integer, Integer, Integer, Integer] {

  /**
   * Setter for <code>scl.match.id</code>.
   */
  def setId(value : Integer) : Unit = {
    set(0, value)
  }

  /**
   * Getter for <code>scl.match.id</code>.
   */
  def getId : Integer = {
    val r = get(0)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.match.week</code>.
   */
  def setWeek(value : Integer) : Unit = {
    set(1, value)
  }

  /**
   * Getter for <code>scl.match.week</code>.
   */
  def getWeek : Integer = {
    val r = get(1)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.match.league</code>.
   */
  def setLeague(value : Integer) : Unit = {
    set(2, value)
  }

  /**
   * Getter for <code>scl.match.league</code>.
   */
  def getLeague : Integer = {
    val r = get(2)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.match.player1</code>.
   */
  def setPlayer1(value : Integer) : Unit = {
    set(3, value)
  }

  /**
   * Getter for <code>scl.match.player1</code>.
   */
  def getPlayer1 : Integer = {
    val r = get(3)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.match.player2</code>.
   */
  def setPlayer2(value : Integer) : Unit = {
    set(4, value)
  }

  /**
   * Getter for <code>scl.match.player2</code>.
   */
  def getPlayer2 : Integer = {
    val r = get(4)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.match.status</code>.
   */
  def setStatus(value : Integer) : Unit = {
    set(5, value)
  }

  /**
   * Getter for <code>scl.match.status</code>.
   */
  def getStatus : Integer = {
    val r = get(5)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  // -------------------------------------------------------------------------
  // Primary key information
  // -------------------------------------------------------------------------
  override def key() : Record1[Integer] = {
    return super.key.asInstanceOf[ Record1[Integer] ]
  }

  // -------------------------------------------------------------------------
  // Record6 type implementation
  // -------------------------------------------------------------------------

  override def fieldsRow : Row6[Integer, Integer, Integer, Integer, Integer, Integer] = {
    super.fieldsRow.asInstanceOf[ Row6[Integer, Integer, Integer, Integer, Integer, Integer] ]
  }

  override def valuesRow : Row6[Integer, Integer, Integer, Integer, Integer, Integer] = {
    super.valuesRow.asInstanceOf[ Row6[Integer, Integer, Integer, Integer, Integer, Integer] ]
  }
  override def field1 : Field[Integer] = Match.MATCH.ID
  override def field2 : Field[Integer] = Match.MATCH.WEEK
  override def field3 : Field[Integer] = Match.MATCH.LEAGUE
  override def field4 : Field[Integer] = Match.MATCH.PLAYER1
  override def field5 : Field[Integer] = Match.MATCH.PLAYER2
  override def field6 : Field[Integer] = Match.MATCH.STATUS
  override def value1 : Integer = getId
  override def value2 : Integer = getWeek
  override def value3 : Integer = getLeague
  override def value4 : Integer = getPlayer1
  override def value5 : Integer = getPlayer2
  override def value6 : Integer = getStatus

  override def value1(value : Integer) : MatchRecord = {
    setId(value)
    this
  }

  override def value2(value : Integer) : MatchRecord = {
    setWeek(value)
    this
  }

  override def value3(value : Integer) : MatchRecord = {
    setLeague(value)
    this
  }

  override def value4(value : Integer) : MatchRecord = {
    setPlayer1(value)
    this
  }

  override def value5(value : Integer) : MatchRecord = {
    setPlayer2(value)
    this
  }

  override def value6(value : Integer) : MatchRecord = {
    setStatus(value)
    this
  }

  override def values(value1 : Integer, value2 : Integer, value3 : Integer, value4 : Integer, value5 : Integer, value6 : Integer) : MatchRecord = {
    this.value1(value1)
    this.value2(value2)
    this.value3(value3)
    this.value4(value4)
    this.value5(value5)
    this.value6(value6)
    this
  }

  /**
   * Create a detached, initialised MatchRecord
   */
  def this(id : Integer, week : Integer, league : Integer, player1 : Integer, player2 : Integer, status : Integer) = {
    this()

    set(0, id)
    set(1, week)
    set(2, league)
    set(3, player1)
    set(4, player2)
    set(5, status)
  }
}
