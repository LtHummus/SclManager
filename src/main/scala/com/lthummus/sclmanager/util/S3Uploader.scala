package com.lthummus.sclmanager.util

import awscala.CredentialsLoader
import awscala.s3.{Bucket, S3}
import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider}
import com.amazonaws.services.s3.model.ObjectMetadata
import com.typesafe.config.ConfigFactory

import scalaz._
import Scalaz._

import awscala._

class S3Uploader {
  val config = ConfigFactory.load()

  implicit val region = Region.default()
  implicit val s3 = S3("", "")

  lazy val ourBucket = s3.bucket(config.getString("s3.bucketName"))

  private def internalPut(name: String, input: Array[Byte], bucket: Bucket) = {
    val metadata = new ObjectMetadata()
    try {
      val result = bucket.putObject(name, input, metadata)
      "https://s3-us-west-2.amazonaws.com/scl-replays-season3/%s".format(name).right
    } catch {
      case e: Exception => e.getMessage.left
    }
  }

  def putReplay(name: String, input: Array[Byte]): String \/ String = {
    ourBucket match {
      case None => s"No bucket found with name ${config.getString("s3.bucketName")}".left
      case Some(bucket) => internalPut(name, input, bucket)
    }
  }
}

object S3Uploader extends App {
  val path = "F:\\SpyPartyReplay-20160611-17-03-00-krazycaley-vs-magician1099-QFaOf6HZRFubm3LBicu27Q-v18.zip"

  val bytes = scala.io.Source.fromFile(path, "ISO-8859-1").map(_.toByte).toArray

  new S3Uploader().putReplay("test3.zip", bytes)
}
