/**
 * This class is generated by jOOQ
 */
package zzz.generated.tables.records


import java.lang.Integer
import java.lang.String

import javax.annotation.Generated

import org.jooq.Field
import org.jooq.Record1
import org.jooq.Record8
import org.jooq.Row8
import org.jooq.impl.UpdatableRecordImpl

import scala.Array

import zzz.generated.tables.Game


/**
 * This class is generated by jOOQ.
 */
@Generated(
  value = Array(
    "http://www.jooq.org",
    "jOOQ version:3.8.4"
  ),
  comments = "This class is generated by jOOQ"
)
class GameRecord extends UpdatableRecordImpl[GameRecord](Game.GAME) with Record8[Integer, String, String, Integer, Integer, Integer, String, String] {

  /**
   * Setter for <code>scl.game.id</code>.
   */
  def setId(value : Integer) : Unit = {
    set(0, value)
  }

  /**
   * Getter for <code>scl.game.id</code>.
   */
  def getId : Integer = {
    val r = get(0)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.game.spy</code>.
   */
  def setSpy(value : String) : Unit = {
    set(1, value)
  }

  /**
   * Getter for <code>scl.game.spy</code>.
   */
  def getSpy : String = {
    val r = get(1)
    if (r == null) null else r.asInstanceOf[String]
  }

  /**
   * Setter for <code>scl.game.sniper</code>.
   */
  def setSniper(value : String) : Unit = {
    set(2, value)
  }

  /**
   * Getter for <code>scl.game.sniper</code>.
   */
  def getSniper : String = {
    val r = get(2)
    if (r == null) null else r.asInstanceOf[String]
  }

  /**
   * Setter for <code>scl.game.bout</code>.
   */
  def setBout(value : Integer) : Unit = {
    set(3, value)
  }

  /**
   * Getter for <code>scl.game.bout</code>.
   */
  def getBout : Integer = {
    val r = get(3)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.game.result</code>.
   */
  def setResult(value : Integer) : Unit = {
    set(4, value)
  }

  /**
   * Getter for <code>scl.game.result</code>.
   */
  def getResult : Integer = {
    val r = get(4)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.game.sequence</code>.
   */
  def setSequence(value : Integer) : Unit = {
    set(5, value)
  }

  /**
   * Getter for <code>scl.game.sequence</code>.
   */
  def getSequence : Integer = {
    val r = get(5)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.game.venue</code>.
   */
  def setVenue(value : String) : Unit = {
    set(6, value)
  }

  /**
   * Getter for <code>scl.game.venue</code>.
   */
  def getVenue : String = {
    val r = get(6)
    if (r == null) null else r.asInstanceOf[String]
  }

  /**
   * Setter for <code>scl.game.gametype</code>.
   */
  def setGametype(value : String) : Unit = {
    set(7, value)
  }

  /**
   * Getter for <code>scl.game.gametype</code>.
   */
  def getGametype : String = {
    val r = get(7)
    if (r == null) null else r.asInstanceOf[String]
  }

  // -------------------------------------------------------------------------
  // Primary key information
  // -------------------------------------------------------------------------
  override def key() : Record1[Integer] = {
    return super.key.asInstanceOf[ Record1[Integer] ]
  }

  // -------------------------------------------------------------------------
  // Record8 type implementation
  // -------------------------------------------------------------------------

  override def fieldsRow : Row8[Integer, String, String, Integer, Integer, Integer, String, String] = {
    super.fieldsRow.asInstanceOf[ Row8[Integer, String, String, Integer, Integer, Integer, String, String] ]
  }

  override def valuesRow : Row8[Integer, String, String, Integer, Integer, Integer, String, String] = {
    super.valuesRow.asInstanceOf[ Row8[Integer, String, String, Integer, Integer, Integer, String, String] ]
  }
  override def field1 : Field[Integer] = Game.GAME.ID
  override def field2 : Field[String] = Game.GAME.SPY
  override def field3 : Field[String] = Game.GAME.SNIPER
  override def field4 : Field[Integer] = Game.GAME.BOUT
  override def field5 : Field[Integer] = Game.GAME.RESULT
  override def field6 : Field[Integer] = Game.GAME.SEQUENCE
  override def field7 : Field[String] = Game.GAME.VENUE
  override def field8 : Field[String] = Game.GAME.GAMETYPE
  override def value1 : Integer = getId
  override def value2 : String = getSpy
  override def value3 : String = getSniper
  override def value4 : Integer = getBout
  override def value5 : Integer = getResult
  override def value6 : Integer = getSequence
  override def value7 : String = getVenue
  override def value8 : String = getGametype

  override def value1(value : Integer) : GameRecord = {
    setId(value)
    this
  }

  override def value2(value : String) : GameRecord = {
    setSpy(value)
    this
  }

  override def value3(value : String) : GameRecord = {
    setSniper(value)
    this
  }

  override def value4(value : Integer) : GameRecord = {
    setBout(value)
    this
  }

  override def value5(value : Integer) : GameRecord = {
    setResult(value)
    this
  }

  override def value6(value : Integer) : GameRecord = {
    setSequence(value)
    this
  }

  override def value7(value : String) : GameRecord = {
    setVenue(value)
    this
  }

  override def value8(value : String) : GameRecord = {
    setGametype(value)
    this
  }

  override def values(value1 : Integer, value2 : String, value3 : String, value4 : Integer, value5 : Integer, value6 : Integer, value7 : String, value8 : String) : GameRecord = {
    this.value1(value1)
    this.value2(value2)
    this.value3(value3)
    this.value4(value4)
    this.value5(value5)
    this.value6(value6)
    this.value7(value7)
    this.value8(value8)
    this
  }

  /**
   * Create a detached, initialised GameRecord
   */
  def this(id : Integer, spy : String, sniper : String, bout : Integer, result : Integer, sequence : Integer, venue : String, gametype : String) = {
    this()

    set(0, id)
    set(1, spy)
    set(2, sniper)
    set(3, bout)
    set(4, result)
    set(5, sequence)
    set(6, venue)
    set(7, gametype)
  }
}
