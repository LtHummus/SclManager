package com.lthummus.sclmanager.parsing

import scalaz._
import Scalaz._

/*
0x3A30C326": //"Beginner v. Beginner High-Rise",
0x5996FAAA": //"Beginner v. Beginner Ballroom",
0x5B121925": //"New Art Ballroom", //works
0x1A56C5A1": //"High-Rise",  //works
0x28B3AA5E": //"Gallery",
0x290A0C75": //"Courtyard 2",  //works
0x3695F583": //"Panopticon",
0x-57415F6F" // "Veranda",
0x-4776E044" // "Balcony",
0xD027340":  //Crowded Pub",
0x3B85FFF3": //"Pub",
0x9C2E7B0":  //Old Ballroom",
0x-4B309795" // "Courtyard 1",
0x7076E38F": //"Double Modern",
0x-C19EB9F": //"Modern",
0x-77FDB7D6" // "Old High-Rise"
 */
case class Level(name: String, checksum: Int)

object Level {
  def AllLevels = Seq(
    Level("BvB High-Rise", 0x3A30C326),
    Level("BvB Ballroom", 0x5996FAAA),
    Level("Ballroom", 0x5B121925),
    Level("High-Rise", 0x1A56C5A1),
    Level("Old Gallery", 0x28B3AA5E),
    Level("Old Courtyard 2", 0x290A0C75),
    Level("Panopticon", 0x3695F583),
    Level("Old Veranda", -0x57415F6F),
    Level("Old Balcony", -0x4776E044),
    Level("Crowded Pub", 0xD027340),
    Level("Old Ballroom", 0x9C2E7B0),
    Level("Old Courtyard 1", -0x4B309795),
    Level("Double Modern", 0x7076E38F),
    Level("Modern", -0xC19EB9F),
    Level("Pub", 0x3B85FFF3),
    Level("Veranda", 1870767448),
    Level("Courtyard", -1647985826),
    Level("Library", 378490722),
    Level("Balcony", 498961985),
    Level("Gallery", 1903409343),
    Level("Terrace", -1875718622),
    Level("Moderne", 775418203),
    Level("Teien", 2044698831)
  )

  def getLevelByName(name: String): String \/ Level = {
    val results = AllLevels.filter(_.name == name)
    results.size match {
      //XXX: CASE = 0 IS A DIRTY HACK -- used for backwards compatibility in older map pools
      //     this shouldn't matter in practice, because the parser doesn't actually use
      //     this function
      case 0 => Level(name, -1).right
      case 1 => results.head.right
      case _ => s"Multiple maps found with the name $name".left
    }
  }
}
