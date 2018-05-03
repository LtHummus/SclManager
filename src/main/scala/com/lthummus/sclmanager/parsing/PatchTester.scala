package com.lthummus.sclmanager.parsing

import java.io.{ByteArrayInputStream, DataInputStream}
import java.nio.file.{Files, Paths}

import scalaz.{-\/, \/-}

object PatchTester extends App {

  val file = "C:\\Users\\Benjamin\\AppData\\Local\\SpyParty\\replays\\Matches\\2018-04\\lthummus vs s76561198016757660%2fsteam - 20180429-13-13-34\\SpyPartyReplay-20180429-13-14-04-lthummus-vs-s76561198016757660%2fsteam-iP61gRp6T7CD9cbVcaJ8PA-v23.replay"

  val bytes = Files.readAllBytes(Paths.get(file))

  ReplayNamePatcher.patchReplay(new DataInputStream(new ByteArrayInputStream(bytes)), Map("lthummus" -> "captfalafel", "s76561198016757660/steam" -> "drmcsteamy")) match {
    case -\/(error) => println(s"Something screwed up $error")
    case \/-(newReplay) =>
      Files.write(Paths.get("C:\\Users\\Benjamin\\AppData\\Local\\SpyParty\\replays\\Matches\\2018-04\\lthummus vs s76561198016757660%2fsteam - 20180429-13-13-34\\maybe.replay"), newReplay)
      println("maybe?")
  }

}
