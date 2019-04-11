package com.lthummus.sclmanager.scaffolding

object SclManagerConfig {

  val databaseUrl: String = sys.env("DATABASE_URL")
  val databaseUsername: String = sys.env("DATABASE_USER")
  val databasePassword: String = sys.env("DATABASE_PASSWORD")

  val s3BucketName: String = sys.env("S3_BUCKET")
  val sharedSecret: String = sys.env("SHARED_SECRET")
  val forfeitPassword: String = sys.env("FORFEIT_PASSWORD")

  val port: Int = sys.env("PORT").toInt
  val webroot: String = sys.env("WEBROOT")

  val s3AccessKey: String = sys.env("AWS_ACCESS_KEY_ID")
  val s3SecretKey: String = sys.env("AWS_SECRET_ACCESS_KEY")

  val sclSeasonNumber: Int = sys.env("SCL_SEASON_NUMBER").toInt
  val seasonLength: Int = sys.env("SCL_SEASON_LENGTH").toInt

  val discordResultsWebhook: String = sys.env("DISCORD_RESULTS_WEBHOOK")

  val serverHost: String = sys.env("SERVER_HOST")

  val debugMode: Boolean = sys.env.get("SCL_DEBUG").exists(_.toBoolean)

  val enableSpypartyFans: Boolean = sys.env.get("ENABLE_SPYPARTY_FANS_HOOK").exists(_.toBoolean)
  val spypartyFansHookUrl: String = sys.env("SPYPARTY_FANS_HOOK_URL")

  val logFormat: String = sys.env.getOrElse("JETTY_LOG_FORMAT", "%{client}a - %u %t \"%r\" %s %O \"%{Referer}i\" \"%{User-Agent}i\"")

}
