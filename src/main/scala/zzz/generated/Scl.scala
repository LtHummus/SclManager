/*
 * This file is generated by jOOQ.
 */
package zzz.generated


import java.util.ArrayList
import java.util.Arrays
import java.util.List

import javax.annotation.Generated

import org.jooq.Catalog
import org.jooq.Table
import org.jooq.impl.SchemaImpl

import scala.Array

import zzz.generated.tables.Bout
import zzz.generated.tables.Division
import zzz.generated.tables.Draft
import zzz.generated.tables.Game
import zzz.generated.tables.Player


object Scl {

  /**
   * The reference instance of <code>scl</code>
   */
  val SCL = new Scl
}

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
class Scl extends SchemaImpl("scl", DefaultCatalog.DEFAULT_CATALOG) {

  override def getCatalog : Catalog = DefaultCatalog.DEFAULT_CATALOG

  override def getTables : List[Table[_]] = {
    val result = new ArrayList[Table[_]]
    result.addAll(getTables0)
    result
  }

  private def getTables0(): List[Table[_]] = {
    return Arrays.asList[Table[_]](
      Bout.BOUT,
      Division.DIVISION,
      Draft.DRAFT,
      Game.GAME,
      Player.PLAYER)
  }
}
