package com.lthummus.sclmanager.parsing

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
    Level("Gallery", 0x28B3AA5E),
    Level("Courtyard 2", 0x290A0C75),
    Level("Panopticon", 0x3695F583),
    Level("Veranda", -0x57415F6F),
    Level("Balcony", -0x4776E044),
    Level("Crowded Pub", 0xD027340),
    Level("Old Ballroom", 0x9C2E7B0),
    Level("Courtyard 1", -0x4B309795),
    Level("Double Modern", 0x7076E38F),
    Level("Modern", -0xC19EB9F),
    Level("Pub", 0x3B85FFF3)
  )
}
