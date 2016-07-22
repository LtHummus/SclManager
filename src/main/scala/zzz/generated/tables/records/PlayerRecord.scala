/**
 * This class is generated by jOOQ
 */
package zzz.generated.tables.records


import java.lang.Integer
import java.lang.String

import javax.annotation.Generated

import org.jooq.Field
import org.jooq.Record1
import org.jooq.Record6
import org.jooq.Row6
import org.jooq.impl.UpdatableRecordImpl

import scala.Array

import zzz.generated.tables.Player


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
class PlayerRecord extends UpdatableRecordImpl[PlayerRecord](Player.PLAYER) with Record6[String, String, Integer, Integer, Integer, String] {

  /**
   * Setter for <code>scl.player.name</code>.
   */
  def setName(value : String) : Unit = {
    set(0, value)
  }

  /**
   * Getter for <code>scl.player.name</code>.
   */
  def getName : String = {
    val r = get(0)
    if (r == null) null else r.asInstanceOf[String]
  }

  /**
   * Setter for <code>scl.player.division</code>.
   */
  def setDivision(value : String) : Unit = {
    set(1, value)
  }

  /**
   * Getter for <code>scl.player.division</code>.
   */
  def getDivision : String = {
    val r = get(1)
    if (r == null) null else r.asInstanceOf[String]
  }

  /**
   * Setter for <code>scl.player.wins</code>.
   */
  def setWins(value : Integer) : Unit = {
    set(2, value)
  }

  /**
   * Getter for <code>scl.player.wins</code>.
   */
  def getWins : Integer = {
    val r = get(2)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.player.draws</code>.
   */
  def setDraws(value : Integer) : Unit = {
    set(3, value)
  }

  /**
   * Getter for <code>scl.player.draws</code>.
   */
  def getDraws : Integer = {
    val r = get(3)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.player.losses</code>.
   */
  def setLosses(value : Integer) : Unit = {
    set(4, value)
  }

  /**
   * Getter for <code>scl.player.losses</code>.
   */
  def getLosses : Integer = {
    val r = get(4)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.player.country</code>.
   */
  def setCountry(value : String) : Unit = {
    set(5, value)
  }

  /**
   * Getter for <code>scl.player.country</code>.
   */
  def getCountry : String = {
    val r = get(5)
    if (r == null) null else r.asInstanceOf[String]
  }

  // -------------------------------------------------------------------------
  // Primary key information
  // -------------------------------------------------------------------------
  override def key() : Record1[String] = {
    return super.key.asInstanceOf[ Record1[String] ]
  }

  // -------------------------------------------------------------------------
  // Record6 type implementation
  // -------------------------------------------------------------------------

  override def fieldsRow : Row6[String, String, Integer, Integer, Integer, String] = {
    super.fieldsRow.asInstanceOf[ Row6[String, String, Integer, Integer, Integer, String] ]
  }

  override def valuesRow : Row6[String, String, Integer, Integer, Integer, String] = {
    super.valuesRow.asInstanceOf[ Row6[String, String, Integer, Integer, Integer, String] ]
  }
  override def field1 : Field[String] = Player.PLAYER.NAME
  override def field2 : Field[String] = Player.PLAYER.DIVISION
  override def field3 : Field[Integer] = Player.PLAYER.WINS
  override def field4 : Field[Integer] = Player.PLAYER.DRAWS
  override def field5 : Field[Integer] = Player.PLAYER.LOSSES
  override def field6 : Field[String] = Player.PLAYER.COUNTRY
  override def value1 : String = getName
  override def value2 : String = getDivision
  override def value3 : Integer = getWins
  override def value4 : Integer = getDraws
  override def value5 : Integer = getLosses
  override def value6 : String = getCountry

  override def value1(value : String) : PlayerRecord = {
    setName(value)
    this
  }

  override def value2(value : String) : PlayerRecord = {
    setDivision(value)
    this
  }

  override def value3(value : Integer) : PlayerRecord = {
    setWins(value)
    this
  }

  override def value4(value : Integer) : PlayerRecord = {
    setDraws(value)
    this
  }

  override def value5(value : Integer) : PlayerRecord = {
    setLosses(value)
    this
  }

  override def value6(value : String) : PlayerRecord = {
    setCountry(value)
    this
  }

  override def values(value1 : String, value2 : String, value3 : Integer, value4 : Integer, value5 : Integer, value6 : String) : PlayerRecord = {
    this.value1(value1)
    this.value2(value2)
    this.value3(value3)
    this.value4(value4)
    this.value5(value5)
    this.value6(value6)
    this
  }

  /**
   * Create a detached, initialised PlayerRecord
   */
  def this(name : String, division : String, wins : Integer, draws : Integer, losses : Integer, country : String) = {
    this()

    set(0, name)
    set(1, division)
    set(2, wins)
    set(3, draws)
    set(4, losses)
    set(5, country)
  }
}
