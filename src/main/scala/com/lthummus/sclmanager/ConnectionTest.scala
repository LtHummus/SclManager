package com.lthummus.sclmanager

import java.sql.DriverManager

import org.jooq.SQLDialect
import org.jooq.impl.DSL
import zzz.generated.Tables
import zzz.generated.tables.{League, Player}


object ConnectionTest extends App {

  val userName = "root"
  val password = "root"
  val url = "jdbc:mysql://192.168.1.46:3306/scl"

  Class.forName("com.mysql.jdbc.Driver").newInstance()
  val conn = DriverManager.getConnection(url, userName, password)


  val context = DSL.using(conn, SQLDialect.MYSQL)

//  val record = context.newRecord(Tables.PLAYER)
//  record.setName("elvisnake")
//  record.setLeague(1)
//  context.executeInsert(record)


  val res = context.selectFrom(Tables.LEAGUE).fetch()
  val res2 = context.selectFrom(Tables.PLAYER).fetch()

  val rec = new Player()


  println(res)

  println(res2)
  //conn.close()
}
