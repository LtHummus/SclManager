package com.lthummus.sclmanager.database

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.jooq.{DSLContext, SQLDialect}
import org.jooq.impl.DSL


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
