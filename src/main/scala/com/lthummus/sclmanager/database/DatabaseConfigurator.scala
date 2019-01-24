package com.lthummus.sclmanager.database

import com.lthummus.sclmanager.scaffolding.SclManagerConfig
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.jooq._
import org.jooq.impl.{DSL, SclTransactionContext}

object DatabaseConfigurator {

  def getDslContext: DSLContext = {
    val jdbcUrl = SclManagerConfig.databaseUrl
    val username = SclManagerConfig.databaseUsername
    val password = SclManagerConfig.databasePassword

    val hikariConfig = new HikariConfig()
    hikariConfig.setJdbcUrl(jdbcUrl)
    hikariConfig.setUsername(username)
    hikariConfig.setPassword(password)

    val hikariDataSource = new HikariDataSource(hikariConfig)

    DSL.using(hikariDataSource, SQLDialect.MYSQL)
  }
}


trait TransactionSupport {
  def insideTransaction[T](func: DSLContext => T)(implicit dslContext: DSLContext): T = {
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