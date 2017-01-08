package com.lthummus.sclmanager.util

import java.io.ByteArrayInputStream

import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{Bucket, ObjectMetadata}
import com.typesafe.config.ConfigFactory

import scalaz._
import Scalaz._

class S3Uploader {
  val config = ConfigFactory.load()

  val S3 = new AmazonS3Client()

  val BucketName = config.getString("s3.bucketName")

  def putReplay(name: String, input: Array[Byte]): String \/ String = {
    "good".right
//    val metadata = new ObjectMetadata()
//    metadata.setContentLength(input.length)
//
//    try {
//      S3.putObject(BucketName, name, new ByteArrayInputStream(input), metadata)
//      s"https://s3-us-west-2.amazonaws.com/$BucketName/$name".right
//    } catch {
//      case e: Exception => e.getMessage.left
//    }
  }
}

object S3Uploader extends App {
  val path = "F:\\test.zip"

  val bytes = scala.io.Source.fromFile(path, "ISO-8859-1").map(_.toByte).toArray

  new S3Uploader().putReplay("aaaa.zip", bytes)
}
