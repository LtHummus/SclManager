package com.lthummus.sclmanager.discord

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object DiscordTest extends App {

  val message =
    """
      |Hello SCL Participant! This is to let you know that week 4 has begun! Your opponent this week is: `canadianbacon`
      |
      |As a reminder, you have not played **1** past match:
      |Week 2 vs. `warningtrack`
      |
      |Please play this match soon to avoid a forfeit!
      |
      |Good luck!
      |""".stripMargin

  val secret = "NOPE"

  val client = new JDABuilder(secret).build()

  client.awaitReady()

  client.addEventListener(DiscordListener)


//  client.getUserById(84411126619578368L).openPrivateChannel().queue(x => {
//    //println("opened")
//    x.sendMessage(message).queue()
//  })

  //channel.sendMessage("Hello world!")

}
