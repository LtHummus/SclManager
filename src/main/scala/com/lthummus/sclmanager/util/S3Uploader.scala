package com.lthummus.sclmanager.util

import java.io.ByteArrayInputStream
import java.net.URLEncoder

import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{Bucket, ObjectMetadata}
import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}

import scalaz._
import Scalaz._

class S3Uploader {
  val config = ConfigFactory.load()

  val S3 = new AmazonS3Client()

  val BucketName = config.getString("s3.bucketName")

  def putReplay(name: String, input: Array[Byte]): String \/ String = {
    val metadata = new ObjectMetadata()
    metadata.setContentLength(input.length)

    try {
      S3.putObject(BucketName, name, new ByteArrayInputStream(input), metadata)
      s"https://s3-us-west-2.amazonaws.com/$BucketName/${URLEncoder.encode(name, "UTF-8")}".right
    } catch {
      case e: Exception =>
        S3Uploader.Logger.warn("Error uploading", e)
        e.getMessage.left
    }
  }
}

object S3Uploader {
  val Logger: Logger = LoggerFactory.getLogger(classOf[S3Uploader])
}
