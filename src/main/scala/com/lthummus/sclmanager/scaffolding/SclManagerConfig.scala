package com.lthummus.sclmanager.scaffolding

import com.typesafe.config.ConfigFactory

object SclManagerConfig {


  private val config: Map[String, String] = if (sys.env.get("CONFIG_SOURCE").contains("ENV")) {
    Map(
      "DATABASE_URL" -> sys.env("DATABASE_URL"),
      "DATABASE_USER" -> sys.env("DATABASE_USER"),
      "DATABASE_PASSWORD" -> sys.env("DATABASE_PASSWORD"),

      "S3_BUCKET" -> sys.env("S3_BUCKET"),
      "SHARED_SECRET" -> sys.env("SHARED_SECRET"),
      "FORFEIT_PASSWORD" -> sys.env("FORFEIT_PASSWORD"),

      "PORT" -> sys.env("PORT"),
      "WEBROOT" -> sys.env("WEBROOT"),

      "AWS_ACCESS_KEY_ID" -> sys.env("AWS_ACCESS_KEY_ID"),
      "AWS_SECRET_ACCESS_KEY" -> sys.env("AWS_SECRET_ACCESS_KEY"),

      "SCL_SEASON_NUMBER" -> sys.env("SCL_SEASON_NUMBER"),
      "SCL_DEBUG" -> sys.env("SCL_DEBUG")

    )
  } else {
    val config = ConfigFactory.load()

    Map(
      "DATABASE_URL" -> config.getString("database.url"),
      "DATABASE_USER" -> config.getString("database.username"),
      "DATABASE_PASSWORD" -> config.getString("database.password"),

      "S3_BUCKET" -> config.getString("s3.bucketName"),
      "SHARED_SECRET" -> config.getString("sharedSecret"),
      "FORFEIT_PASSWORD" -> config.getString("forefitPassword"),

      "PORT" -> config.getString("server.port"),
      "WEBROOT" -> config.getString("server.webroot"),

      "AWS_ACCESS_KEY_ID" -> config.getString("aws.accessKey"),
      "AWS_SECRET_ACCESS_KEY" -> config.getString("aws.secretKey"),

      "SCL_SEASON_NUMBER" -> config.getString("scl.seasonNumber"),
      "SCL_DEBUG" -> config.getString("scl.debug")


    )
  }

  def databaseUrl: String = config("DATABASE_URL")
  def databaseUsername: String = config("DATABASE_USER")
  def databasePassword: String = config("DATABASE_PASSWORD")

  def s3BucketName: String = config("S3_BUCKET")
  def sharedSecret: String = config("SHARED_SECRET")
  def forfeitPassword: String = config("FORFEIT_PASSWORD")

  def port: Int = config("PORT").toInt
  def webroot: String = config("WEBROOT")

  def s3AccessKey: String = config("AWS_ACCESS_KEY_ID")
  def s3SecretKey: String = config("AWS_SECRET_ACCESS_KEY")

  def sclSeasonNumber: Int = config("SCL_SEASON_NUMBER").toInt

  def debugMode: Boolean = config.get("SCL_DEBUG").exists(_.toBoolean)

}
