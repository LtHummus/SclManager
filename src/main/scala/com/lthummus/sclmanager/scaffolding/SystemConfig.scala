package com.lthummus.sclmanager.scaffolding

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.Base64

import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.kms.AWSKMSClientBuilder
import com.amazonaws.services.kms.model.{DecryptRequest, EncryptRequest}
import com.typesafe.config.Config
import org.slf4j.LoggerFactory

object SystemConfig {

  private val Logger = LoggerFactory.getLogger("SystemConfig")

  private val KeyManager = AWSKMSClientBuilder.standard().withRegion(Regions.US_WEST_1).build()

  val Stage: String = Option(System.getenv("stage")).getOrElse("test")

  def isProduction: Boolean = Stage == "prod"
  def isTest: Boolean = !isProduction


  implicit class TieredConfig(config: Config) {

    Logger.info(s"Using stage $Stage")

    private def stageifyKey(key: String) = s"$Stage.$key"

    def getStringWithStage(key: String): String = {
      config.getString(stageifyKey(key))
    }

    def getIntWithStage(key: String): Int = {
      config.getInt(stageifyKey(key))
    }

    def getEncryptedString(key: String): String = {
      val base64Text = config.getString(stageifyKey(key))
      val encryptedData = Base64.getDecoder.decode(base64Text)
      val decryptRequest = new DecryptRequest().withCiphertextBlob(ByteBuffer.wrap(encryptedData))
      val plaintextArray = KeyManager.decrypt(decryptRequest).getPlaintext.array()
      new String(plaintextArray, StandardCharsets.UTF_8)
    }


  }

}
