package com.lthummus.sclmanager.util

import java.io.ByteArrayInputStream

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import org.slf4j.LoggerFactory
import scalaz._
import Scalaz._
import com.amazonaws.auth.BasicAWSCredentials
import com.lthummus.sclmanager.scaffolding.SclManagerConfig

object S3Uploader {
  private val Logger = LoggerFactory.getLogger("S3Uploader")
}

class S3Uploader {
  import S3Uploader._

  private val S3 = new AmazonS3Client(new BasicAWSCredentials(SclManagerConfig.s3AccessKey, SclManagerConfig.s3SecretKey))

  private val BucketName = SclManagerConfig.s3BucketName

  def putReplay(name: String, input: Array[Byte]): String \/ String = {
    val metadata = new ObjectMetadata()
    metadata.setContentLength(input.length)

    try {
      S3.putObject(BucketName, name, new ByteArrayInputStream(input), metadata)
      Logger.info("Successfully uploaded replay ZIP file {}", name)

      s"https://s3-us-west-2.amazonaws.com/$BucketName/$name".right
    } catch {
      case e: Exception =>
        Logger.warn("Error uploading", e)
        e.getMessage.left
    }
  }
}


