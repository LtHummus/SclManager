package com.lthummus.sclmanager.scaffolding

object SclManagerConfig {

  def databaseUrl: String = sys.env("DATABASE_URL")
  def databaseUsername: String = sys.env("DATABASE_USER")
  def databasePassword: String = sys.env("DATABASE_PASSWORD")

  def s3BucketName: String = sys.env("S3_BUCKET")
  def sharedSecret: String = sys.env("SHARED_SECRET")
  def forfeitPassword: String = sys.env("FORFEIT_PASSWORD")

  def port: Int = sys.env("PORT").toInt
  def webroot: String = sys.env("WEBROOT")

  def s3AccessKey: String = sys.env("AWS_ACCESS_KEY_ID")
  def s3SecretKey: String = sys.env("AWS_SECRET_ACCESS_KEY")

  def debugMode: Boolean = sys.env.get("SCL_DEBUG").exists(_.toBoolean)

}
