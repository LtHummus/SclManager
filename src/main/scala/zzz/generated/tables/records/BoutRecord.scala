/**
 * This class is generated by jOOQ
 */
package zzz.generated.tables.records


import java.lang.Integer
import java.lang.String
import java.sql.Timestamp

import javax.annotation.Generated

import org.jooq.Field
import org.jooq.Record1
import org.jooq.Record11
import org.jooq.Row11
import org.jooq.impl.UpdatableRecordImpl

import scala.Array

import zzz.generated.tables.Bout


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
class BoutRecord extends UpdatableRecordImpl[BoutRecord](Bout.BOUT) with Record11[Integer, Integer, String, String, String, Integer, String, String, Integer, Integer, Timestamp] {

  /**
   * Setter for <code>scl.bout.id</code>.
   */
  def setId(value : Integer) : Unit = {
    set(0, value)
  }

  /**
   * Getter for <code>scl.bout.id</code>.
   */
  def getId : Integer = {
    val r = get(0)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.bout.week</code>.
   */
  def setWeek(value : Integer) : Unit = {
    set(1, value)
  }

  /**
   * Getter for <code>scl.bout.week</code>.
   */
  def getWeek : Integer = {
    val r = get(1)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.bout.division</code>.
   */
  def setDivision(value : String) : Unit = {
    set(2, value)
  }

  /**
   * Getter for <code>scl.bout.division</code>.
   */
  def getDivision : String = {
    val r = get(2)
    if (r == null) null else r.asInstanceOf[String]
  }

  /**
   * Setter for <code>scl.bout.player1</code>.
   */
  def setPlayer1(value : String) : Unit = {
    set(3, value)
  }

  /**
   * Getter for <code>scl.bout.player1</code>.
   */
  def getPlayer1 : String = {
    val r = get(3)
    if (r == null) null else r.asInstanceOf[String]
  }

  /**
   * Setter for <code>scl.bout.player2</code>.
   */
  def setPlayer2(value : String) : Unit = {
    set(4, value)
  }

  /**
   * Getter for <code>scl.bout.player2</code>.
   */
  def getPlayer2 : String = {
    val r = get(4)
    if (r == null) null else r.asInstanceOf[String]
  }

  /**
   * Setter for <code>scl.bout.status</code>.
   */
  def setStatus(value : Integer) : Unit = {
    set(5, value)
  }

  /**
   * Getter for <code>scl.bout.status</code>.
   */
  def getStatus : Integer = {
    val r = get(5)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.bout.winner</code>.
   */
  def setWinner(value : String) : Unit = {
    set(6, value)
  }

  /**
   * Getter for <code>scl.bout.winner</code>.
   */
  def getWinner : String = {
    val r = get(6)
    if (r == null) null else r.asInstanceOf[String]
  }

  /**
   * Setter for <code>scl.bout.match_url</code>.
   */
  def setMatchUrl(value : String) : Unit = {
    set(7, value)
  }

  /**
   * Getter for <code>scl.bout.match_url</code>.
   */
  def getMatchUrl : String = {
    val r = get(7)
    if (r == null) null else r.asInstanceOf[String]
  }

  /**
   * Setter for <code>scl.bout.draft</code>.
   */
  def setDraft(value : Integer) : Unit = {
    set(8, value)
  }

  /**
   * Getter for <code>scl.bout.draft</code>.
   */
  def getDraft : Integer = {
    val r = get(8)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.bout.bout_type</code>.
   */
  def setBoutType(value : Integer) : Unit = {
    set(9, value)
  }

  /**
   * Getter for <code>scl.bout.bout_type</code>.
   */
  def getBoutType : Integer = {
    val r = get(9)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.bout.timestamp</code>.
   */
  def setTimestamp(value : Timestamp) : Unit = {
    set(10, value)
  }

  /**
   * Getter for <code>scl.bout.timestamp</code>.
   */
  def getTimestamp : Timestamp = {
    val r = get(10)
    if (r == null) null else r.asInstanceOf[Timestamp]
  }

  // -------------------------------------------------------------------------
  // Primary key information
  // -------------------------------------------------------------------------
  override def key() : Record1[Integer] = {
    return super.key.asInstanceOf[ Record1[Integer] ]
  }

  // -------------------------------------------------------------------------
  // Record11 type implementation
  // -------------------------------------------------------------------------

  override def fieldsRow : Row11[Integer, Integer, String, String, String, Integer, String, String, Integer, Integer, Timestamp] = {
    super.fieldsRow.asInstanceOf[ Row11[Integer, Integer, String, String, String, Integer, String, String, Integer, Integer, Timestamp] ]
  }

  override def valuesRow : Row11[Integer, Integer, String, String, String, Integer, String, String, Integer, Integer, Timestamp] = {
    super.valuesRow.asInstanceOf[ Row11[Integer, Integer, String, String, String, Integer, String, String, Integer, Integer, Timestamp] ]
  }
  override def field1 : Field[Integer] = Bout.BOUT.ID
  override def field2 : Field[Integer] = Bout.BOUT.WEEK
  override def field3 : Field[String] = Bout.BOUT.DIVISION
  override def field4 : Field[String] = Bout.BOUT.PLAYER1
  override def field5 : Field[String] = Bout.BOUT.PLAYER2
  override def field6 : Field[Integer] = Bout.BOUT.STATUS
  override def field7 : Field[String] = Bout.BOUT.WINNER
  override def field8 : Field[String] = Bout.BOUT.MATCH_URL
  override def field9 : Field[Integer] = Bout.BOUT.DRAFT
  override def field10 : Field[Integer] = Bout.BOUT.BOUT_TYPE
  override def field11 : Field[Timestamp] = Bout.BOUT.TIMESTAMP
  override def value1 : Integer = getId
  override def value2 : Integer = getWeek
  override def value3 : String = getDivision
  override def value4 : String = getPlayer1
  override def value5 : String = getPlayer2
  override def value6 : Integer = getStatus
  override def value7 : String = getWinner
  override def value8 : String = getMatchUrl
  override def value9 : Integer = getDraft
  override def value10 : Integer = getBoutType
  override def value11 : Timestamp = getTimestamp

  override def value1(value : Integer) : BoutRecord = {
    setId(value)
    this
  }

  override def value2(value : Integer) : BoutRecord = {
    setWeek(value)
    this
  }

  override def value3(value : String) : BoutRecord = {
    setDivision(value)
    this
  }

  override def value4(value : String) : BoutRecord = {
    setPlayer1(value)
    this
  }

  override def value5(value : String) : BoutRecord = {
    setPlayer2(value)
    this
  }

  override def value6(value : Integer) : BoutRecord = {
    setStatus(value)
    this
  }

  override def value7(value : String) : BoutRecord = {
    setWinner(value)
    this
  }

  override def value8(value : String) : BoutRecord = {
    setMatchUrl(value)
    this
  }

  override def value9(value : Integer) : BoutRecord = {
    setDraft(value)
    this
  }

  override def value10(value : Integer) : BoutRecord = {
    setBoutType(value)
    this
  }

  override def value11(value : Timestamp) : BoutRecord = {
    setTimestamp(value)
    this
  }

  override def values(value1 : Integer, value2 : Integer, value3 : String, value4 : String, value5 : String, value6 : Integer, value7 : String, value8 : String, value9 : Integer, value10 : Integer, value11 : Timestamp) : BoutRecord = {
    this.value1(value1)
    this.value2(value2)
    this.value3(value3)
    this.value4(value4)
    this.value5(value5)
    this.value6(value6)
    this.value7(value7)
    this.value8(value8)
    this.value9(value9)
    this.value10(value10)
    this.value11(value11)
    this
  }

  /**
   * Create a detached, initialised BoutRecord
   */
  def this(id : Integer, week : Integer, division : String, player1 : String, player2 : String, status : Integer, winner : String, matchUrl : String, draft : Integer, boutType : Integer, timestamp : Timestamp) = {
    this()

    set(0, id)
    set(1, week)
    set(2, division)
    set(3, player1)
    set(4, player2)
    set(5, status)
    set(6, winner)
    set(7, matchUrl)
    set(8, draft)
    set(9, boutType)
    set(10, timestamp)
  }
}
