/**
 * This class is generated by jOOQ
 */
package zzz.generated.tables


import java.lang.Class
import java.lang.Integer
import java.lang.String
import java.util.Arrays
import java.util.List

import javax.annotation.Generated

import org.jooq.Field
import org.jooq.Schema
import org.jooq.Table
import org.jooq.TableField
import org.jooq.UniqueKey
import org.jooq.impl.TableImpl

import scala.Array

import zzz.generated.Keys
import zzz.generated.Scl
import zzz.generated.tables.records.DivisionRecord


object Division {

  /**
   * The reference instance of <code>scl.division</code>
   */
  val DIVISION = new Division
}

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
class Division(alias : String, aliased : Table[DivisionRecord], parameters : Array[ Field[_] ]) extends TableImpl[DivisionRecord](alias, Scl.SCL, aliased, parameters, "") {

  /**
   * The class holding records for this type
   */
  override def getRecordType : Class[DivisionRecord] = {
    classOf[DivisionRecord]
  }

  /**
   * The column <code>scl.division.name</code>.
   */
  val NAME : TableField[DivisionRecord, String] = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(32).nullable(false), "")

  /**
   * The column <code>scl.division.precedence</code>.
   */
  val PRECEDENCE : TableField[DivisionRecord, Integer] = createField("precedence", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), "")

  /**
   * Create a <code>scl.division</code> table reference
   */
  def this() = {
    this("division", null, null)
  }

  /**
   * Create an aliased <code>scl.division</code> table reference
   */
  def this(alias : String) = {
    this(alias, zzz.generated.tables.Division.DIVISION, null)
  }

  private def this(alias : String, aliased : Table[DivisionRecord]) = {
    this(alias, aliased, null)
  }

  override def getSchema : Schema = Scl.SCL

  override def getPrimaryKey : UniqueKey[DivisionRecord] = {
    Keys.KEY_DIVISION_PRIMARY
  }

  override def getKeys : List[ UniqueKey[DivisionRecord] ] = {
    return Arrays.asList[ UniqueKey[DivisionRecord] ](Keys.KEY_DIVISION_PRIMARY)
  }

  override def as(alias : String) : Division = {
    new Division(alias, this)
  }

  /**
   * Rename this table
   */
  def rename(name : String) : Division = {
    new Division(name, null)
  }
}
