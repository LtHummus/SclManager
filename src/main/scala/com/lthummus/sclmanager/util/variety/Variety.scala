package com.lthummus.sclmanager.util.variety

import org.json4s._
import org.json4s.jackson.JsonMethods._

object Variety extends App {

  implicit val formats: Formats = DefaultFormats

  case class DumpEntry(id: Int, draft: DraftEntry)
  case class DraftEntry(payload: DraftPayloadEntry)
  case class DraftPayloadEntry(bannedMaps: Seq[DraftPayloadPickEntry], pickedMaps: Seq[DraftPayloadPickEntry])
  case class DraftPayloadPickEntry(picker: String, map: String)

  val data = scala.io.Source.fromFile("dump.json").getLines().mkString("\n")

  val jsonData = parse(data).extract[Seq[DumpEntry]]

  val bans = jsonData.flatMap(_.draft.payload.bannedMaps).groupBy(_.map).mapValues(_.size)
  val picks = jsonData.flatMap(_.draft.payload.pickedMaps).groupBy(_.map).mapValues(_.size)

  val banTally = bans.toList.sortBy{case (_, count) => count}.reverse.map{ case (map, count) => s"$map,$count"}.mkString("\n")
  val pickTally = picks.toList.sortBy{case (_, count) => count}.reverse.map{ case (map, count) => s"$map,$count"}.mkString("\n")


  println(banTally)
  println()
  println(pickTally)


  val nobans = jsonData.flatMap(_.draft.payload.bannedMaps).groupBy(_.map)("Nothing")
  println(nobans.groupBy(_.picker).mapValues(_.size).toList.sortBy{case (_, times) => times}.reverse.mkString("\n"))


}
