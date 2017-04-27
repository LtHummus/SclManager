package com.lthummus.sclmanager.database

import java.sql.Connection

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.jooq._
import org.jooq.impl.{DSL, DefaultTransactionContext, DefaultTransactionProvider, SclTransactionContext}

object DatabaseConfigurator {

  def getDslContext: DSLContext = {
    val config = ConfigFactory.load()

    val jdbcUrl = config.getString("database.url")
    val username = config.getString("database.username")
    val password = config.getString("database.password")

    val hikariConfig = new HikariConfig()
    hikariConfig.setJdbcUrl(jdbcUrl)
    hikariConfig.setUsername(username)
    hikariConfig.setPassword(password)

    val hikariDataSource = new HikariDataSource(hikariConfig)

    DSL.using(hikariDataSource, SQLDialect.MYSQL)
  }
}


trait TransactionSupport {
  def insideTransaction[T](func: (DSLContext) => T)(implicit dslContext: DSLContext): T = {
    val ctx = new SclTransactionContext(dslContext.configuration().derive)
    val provider = ctx.configuration().transactionProvider()

    try {
      provider.begin(ctx)
      val res = func(DSL.using(ctx.configuration()))
      provider.commit(ctx)
      res
    } catch {
      case e: Exception => provider.rollback(ctx); throw e
    }
  }
}