package com.lthummus.sclmanager.util

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.Base64

import com.amazonaws.regions.Regions
import com.amazonaws.services.kms.AWSKMSClientBuilder
import com.amazonaws.services.kms.model.EncryptRequest


object CredentialEncryptor extends App {



  private val KeyManager = AWSKMSClientBuilder.standard().withRegion(Regions.US_WEST_1).build()
  private val KeyId = "arn:aws:kms:us-west-1:XXXXXX:key/YYYYYYYYY"



  def encryptString(plaintext: String): String = {
    val req = new EncryptRequest()
      .withPlaintext(ByteBuffer.wrap(plaintext.getBytes(StandardCharsets.UTF_8)))
      .withKeyId(KeyId)

    val res = KeyManager.encrypt(req).getCiphertextBlob
    Base64.getEncoder.encodeToString(res.array())
  }

  private val plaintext = "hello"
  println(plaintext)
  println(encryptString(plaintext))


















































}
