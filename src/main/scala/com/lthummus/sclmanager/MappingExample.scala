package com.lthummus.sclmanager

object MappingExample extends App {

  val foo: List[(String, Int)] = List(
    ("hello", 5),
    ("goodbye", 1)
  )

  foo.foreach{ case(a, _) => println(a)}

}
