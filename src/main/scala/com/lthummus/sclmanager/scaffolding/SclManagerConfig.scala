package com.lthummus.sclmanager.scaffolding

import com.typesafe.config.ConfigFactory

object SclManagerConfig {

  private val Config = ConfigFactory.load()

  def databaseUrl: String = sys.env("DATABASE_URL")
  def databaseUsername: String = sys.env("DATABASE_USER")
  def databasePassword: String = sys.env("DATABASE_PASSWORD")

  def s3BucketName: String = sys.env("S3_BUCKET")
  def sharedSecret: String = sys.env("SHARED_SECRET")
  def forfeitPassword: String = sys.env("FORFEIT_PASSWORD")

  def port: Int = sys.env("PORT").toInt
  def webroot: String = sys.env("WEBROOT")

  def debugMode: Boolean = sys.env.get("SCL_DEBUG").exists(_.toBoolean)

}
