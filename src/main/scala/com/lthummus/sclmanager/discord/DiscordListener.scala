package com.lthummus.sclmanager.discord

import java.util.concurrent.ConcurrentHashMap
import java.util.{Timer, TimerTask}

import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.collection.JavaConverters._

object DiscordListener extends ListenerAdapter {
  private val Logger = LoggerFactory.getLogger(getClass)
  private val ChannelId = 102582427678953472L

  private val channels: collection.concurrent.Map[Long, SurveyBot] = new ConcurrentHashMap[Long, SurveyBot]().asScala

  private val CleanUpTask: TimerTask = new TimerTask {
    override def run(): Unit = {
      Logger.info("Running cleanup task")
      channels.filter { case(_, survey) => survey.readyForCleanup }.foreach { case (id, survey) =>
        Logger.info(s"Removing ${survey.author.getAsTag}")
        survey.cleanup()
        channels.remove(id)
      }
    }
  }

  new Timer().scheduleAtFixedRate(CleanUpTask, 0L, 5.minutes.toMillis)

  override def onMessageReceived(event: MessageReceivedEvent): Unit = {
    if (!event.getAuthor.isBot) {
      Logger.info(s"Message From: ${event.getAuthor.getName} In: ${event.getChannel.getName} (${event.getChannel.getType.toString}) - ${event.getMessage.getContentRaw}")
      if (event.getMessage.getChannel.getType == ChannelType.PRIVATE) {
        channels.get(event.getAuthor.getIdLong) match {
          case None => event.getChannel.sendMessage("Sorry, I don't have a survey record for you. Perhaps you took too long?").queue()
          case Some(survey) => survey.gotMessage(event.getMessage)
        }
      } else if (event.getMessage.getContentRaw == "!join" && event.getMessage.getChannel.getIdLong == ChannelId) {
        Logger.info("Should join")
        event.getAuthor.openPrivateChannel().queue(channel => {
          channels.put(event.getAuthor.getIdLong, SurveyBot(event.getAuthor, channel, ChannelId))
          Logger.info(s"Opened private channel with ${event.getAuthor.getName}")
        })
      }
    }
  }
}
