/*
 * This file is generated by jOOQ.
 */
package zzz.generated.tables.records


import java.lang.Boolean
import java.lang.Integer
import java.lang.String

import javax.annotation.Generated

import org.jooq.Field
import org.jooq.Record1
import org.jooq.Record6
import org.jooq.Row6
import org.jooq.impl.UpdatableRecordImpl

import scala.Array

import zzz.generated.tables.Division


/**
 * This class is generated by jOOQ.
 */
@Generated(
  value = Array(
    "http://www.jooq.org",
    "jOOQ version:3.11.11"
  ),
  comments = "This class is generated by jOOQ"
)
class DivisionRecord extends UpdatableRecordImpl[DivisionRecord](Division.DIVISION) with Record6[String, Integer, Boolean, Integer, Integer, Integer] {

  /**
   * Setter for <code>scl.division.name</code>.
   */
  def setName(value : String) : Unit = {
    set(0, value)
  }

  /**
   * Getter for <code>scl.division.name</code>.
   */
  def getName : String = {
    val r = get(0)
    if (r == null) null else r.asInstanceOf[String]
  }

  /**
   * Setter for <code>scl.division.precedence</code>.
   */
  def setPrecedence(value : Integer) : Unit = {
    set(1, value)
  }

  /**
   * Getter for <code>scl.division.precedence</code>.
   */
  def getPrecedence : Integer = {
    val r = get(1)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.division.secret</code>.
   */
  def setSecret(value : Boolean) : Unit = {
    set(2, value)
  }

  /**
   * Getter for <code>scl.division.secret</code>.
   */
  def getSecret : Boolean = {
    val r = get(2)
    if (r == null) null else r.asInstanceOf[Boolean]
  }

  /**
   * Setter for <code>scl.division.win_points</code>.
   */
  def setWinPoints(value : Integer) : Unit = {
    set(3, value)
  }

  /**
   * Getter for <code>scl.division.win_points</code>.
   */
  def getWinPoints : Integer = {
    val r = get(3)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.division.draw_points</code>.
   */
  def setDrawPoints(value : Integer) : Unit = {
    set(4, value)
  }

  /**
   * Getter for <code>scl.division.draw_points</code>.
   */
  def getDrawPoints : Integer = {
    val r = get(4)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  /**
   * Setter for <code>scl.division.loss_points</code>.
   */
  def setLossPoints(value : Integer) : Unit = {
    set(5, value)
  }

  /**
   * Getter for <code>scl.division.loss_points</code>.
   */
  def getLossPoints : Integer = {
    val r = get(5)
    if (r == null) null else r.asInstanceOf[Integer]
  }

  // -------------------------------------------------------------------------
  // Primary key information
  // -------------------------------------------------------------------------
  override def key : Record1[String] = {
    return super.key.asInstanceOf[ Record1[String] ]
  }

  // -------------------------------------------------------------------------
  // Record6 type implementation
  // -------------------------------------------------------------------------

  override def fieldsRow : Row6[String, Integer, Boolean, Integer, Integer, Integer] = {
    super.fieldsRow.asInstanceOf[ Row6[String, Integer, Boolean, Integer, Integer, Integer] ]
  }

  override def valuesRow : Row6[String, Integer, Boolean, Integer, Integer, Integer] = {
    super.valuesRow.asInstanceOf[ Row6[String, Integer, Boolean, Integer, Integer, Integer] ]
  }
  override def field1 : Field[String] = Division.DIVISION.NAME
  override def field2 : Field[Integer] = Division.DIVISION.PRECEDENCE
  override def field3 : Field[Boolean] = Division.DIVISION.SECRET
  override def field4 : Field[Integer] = Division.DIVISION.WIN_POINTS
  override def field5 : Field[Integer] = Division.DIVISION.DRAW_POINTS
  override def field6 : Field[Integer] = Division.DIVISION.LOSS_POINTS
  override def component1 : String = getName
  override def component2 : Integer = getPrecedence
  override def component3 : Boolean = getSecret
  override def component4 : Integer = getWinPoints
  override def component5 : Integer = getDrawPoints
  override def component6 : Integer = getLossPoints
  override def value1 : String = getName
  override def value2 : Integer = getPrecedence
  override def value3 : Boolean = getSecret
  override def value4 : Integer = getWinPoints
  override def value5 : Integer = getDrawPoints
  override def value6 : Integer = getLossPoints

  override def value1(value : String) : DivisionRecord = {
    setName(value)
    this
  }

  override def value2(value : Integer) : DivisionRecord = {
    setPrecedence(value)
    this
  }

  override def value3(value : Boolean) : DivisionRecord = {
    setSecret(value)
    this
  }

  override def value4(value : Integer) : DivisionRecord = {
    setWinPoints(value)
    this
  }

  override def value5(value : Integer) : DivisionRecord = {
    setDrawPoints(value)
    this
  }

  override def value6(value : Integer) : DivisionRecord = {
    setLossPoints(value)
    this
  }

  override def values(value1 : String, value2 : Integer, value3 : Boolean, value4 : Integer, value5 : Integer, value6 : Integer) : DivisionRecord = {
    this.value1(value1)
    this.value2(value2)
    this.value3(value3)
    this.value4(value4)
    this.value5(value5)
    this.value6(value6)
    this
  }

  /**
   * Create a detached, initialised DivisionRecord
   */
  def this(name : String, precedence : Integer, secret : Boolean, winPoints : Integer, drawPoints : Integer, lossPoints : Integer) = {
    this()

    set(0, name)
    set(1, precedence)
    set(2, secret)
    set(3, winPoints)
    set(4, drawPoints)
    set(5, lossPoints)
  }
}
