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
import org.jooq.Record6
import org.jooq.Row6
import org.jooq.impl.UpdatableRecordImpl

import scala.Array

import zzz.generated.tables.Draft


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
class DraftRecord extends UpdatableRecordImpl[DraftRecord](Draft.DRAFT) with Record6[Integer, String, String, String, Timestamp, String] {

  /**
   * Setter for <code>scl.draft.id</code>.
   */
  def setId(value : Integer) : Unit = {
    set(0, value)
  }

  /**
   * Getter for <code>scl.draft.id</code>.
   */
  def getId : Integer = {
    val r = get(0)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.draft.room_code</code>.
   */
  def setRoomCode(value : String) : Unit = {
    set(1, value)
  }

  /**
   * Getter for <code>scl.draft.room_code</code>.
   */
  def getRoomCode : String = {
    val r = get(1)
    if (r == null) null else r.asInstanceOf[String]
  }

  /**
   * Setter for <code>scl.draft.player1</code>.
   */
  def setPlayer1(value : String) : Unit = {
    set(2, value)
  }

  /**
   * Getter for <code>scl.draft.player1</code>.
   */
  def getPlayer1 : String = {
    val r = get(2)
    if (r == null) null else r.asInstanceOf[String]
  }

  /**
   * Setter for <code>scl.draft.player2</code>.
   */
  def setPlayer2(value : String) : Unit = {
    set(3, value)
  }

  /**
   * Getter for <code>scl.draft.player2</code>.
   */
  def getPlayer2 : String = {
    val r = get(3)
    if (r == null) null else r.asInstanceOf[String]
  }

  /**
   * Setter for <code>scl.draft.time</code>.
   */
  def setTime(value : Timestamp) : Unit = {
    set(4, value)
  }

  /**
   * Getter for <code>scl.draft.time</code>.
   */
  def getTime : Timestamp = {
    val r = get(4)
    if (r == null) null else r.asInstanceOf[Timestamp]
  }

  /**
   * Setter for <code>scl.draft.payload</code>.
   */
  def setPayload(value : String) : Unit = {
    set(5, value)
  }

  /**
   * Getter for <code>scl.draft.payload</code>.
   */
  def getPayload : String = {
    val r = get(5)
    if (r == null) null else r.asInstanceOf[String]
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

  override def fieldsRow : Row6[Integer, String, String, String, Timestamp, String] = {
    super.fieldsRow.asInstanceOf[ Row6[Integer, String, String, String, Timestamp, String] ]
  }

  override def valuesRow : Row6[Integer, String, String, String, Timestamp, String] = {
    super.valuesRow.asInstanceOf[ Row6[Integer, String, String, String, Timestamp, String] ]
  }
  override def field1 : Field[Integer] = Draft.DRAFT.ID
  override def field2 : Field[String] = Draft.DRAFT.ROOM_CODE
  override def field3 : Field[String] = Draft.DRAFT.PLAYER1
  override def field4 : Field[String] = Draft.DRAFT.PLAYER2
  override def field5 : Field[Timestamp] = Draft.DRAFT.TIME
  override def field6 : Field[String] = Draft.DRAFT.PAYLOAD
  override def value1 : Integer = getId
  override def value2 : String = getRoomCode
  override def value3 : String = getPlayer1
  override def value4 : String = getPlayer2
  override def value5 : Timestamp = getTime
  override def value6 : String = getPayload

  override def value1(value : Integer) : DraftRecord = {
    setId(value)
    this
  }

  override def value2(value : String) : DraftRecord = {
    setRoomCode(value)
    this
  }

  override def value3(value : String) : DraftRecord = {
    setPlayer1(value)
    this
  }

  override def value4(value : String) : DraftRecord = {
    setPlayer2(value)
    this
  }

  override def value5(value : Timestamp) : DraftRecord = {
    setTime(value)
    this
  }

  override def value6(value : String) : DraftRecord = {
    setPayload(value)
    this
  }

  override def values(value1 : Integer, value2 : String, value3 : String, value4 : String, value5 : Timestamp, value6 : String) : DraftRecord = {
    this.value1(value1)
    this.value2(value2)
    this.value3(value3)
    this.value4(value4)
    this.value5(value5)
    this.value6(value6)
    this
  }

  /**
   * Create a detached, initialised DraftRecord
   */
  def this(id : Integer, roomCode : String, player1 : String, player2 : String, time : Timestamp, payload : String) = {
    this()

    set(0, id)
    set(1, roomCode)
    set(2, player1)
    set(3, player2)
    set(4, time)
    set(5, payload)
  }
}
