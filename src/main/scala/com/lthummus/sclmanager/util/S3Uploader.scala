package com.lthummus.sclmanager.util

import java.io.ByteArrayInputStream

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{Bucket, ObjectMetadata}
import org.slf4j.{Logger, LoggerFactory}
import scalaz._
import Scalaz._
import com.lthummus.sclmanager.scaffolding.SclManagerConfig


class S3Uploader {
  private val S3 = new AmazonS3Client()

  private val BucketName = SclManagerConfig.s3BucketName

  def putReplay(name: String, input: Array[Byte]): String \/ String = {
    val metadata = new ObjectMetadata()
    metadata.setContentLength(input.length)

    try {
      S3.putObject(BucketName, name, new ByteArrayInputStream(input), metadata)
      s"https://s3-us-west-2.amazonaws.com/$BucketName/$name".right
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
