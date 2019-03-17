package com.lthummus.sclmanager.util

import java.io.{DataInputStream, FileInputStream}

import com.lthummus.sclmanager.parsing.Replay

object ParserTest extends App {

  val data = new DataInputStream(new FileInputStream(args(0)))

  println(Replay.fromInputStream(data))

  data.close()
}
