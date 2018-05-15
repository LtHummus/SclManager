package com.lthummus.sclmanager.parsing

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, DataInputStream, InputStream}
import java.util.zip.{ZipEntry, ZipInputStream, ZipOutputStream}

import scalaz._
import Scalaz._
import org.apache.commons.io.IOUtils

object ZipFilePatcher {

  def patchZipFile(zipFileBytes: Array[Byte], nameChanges: Map[String, String]): String \/ Array[Byte] = {
    val baos = new ByteArrayOutputStream()
    val zis = new ZipInputStream(new ByteArrayInputStream(zipFileBytes))
    val zos = new ZipOutputStream(baos)

    var inputEntry: ZipEntry = zis.getNextEntry

    while (inputEntry != null) {
      if (!inputEntry.isDirectory) {
        val fileData = new DataInputStream(zis)
        val newReplay = ReplayNamePatcher.patchReplay(fileData, nameChanges)

        newReplay match {
          case -\/(error) => return s"Error patching replay: $error".left
          case \/-(data) =>
            val newEntry = new ZipEntry(inputEntry.getName)
            zos.putNextEntry(newEntry)
            zos.write(data)
            zos.closeEntry()
        }

      }

      inputEntry = zis.getNextEntry
    }

    zis.close()
    zos.close()

    baos.toByteArray.right
  }
}
