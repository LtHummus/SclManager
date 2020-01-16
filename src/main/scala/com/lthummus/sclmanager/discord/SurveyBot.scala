package com.lthummus.sclmanager.discord

import java.util.Locale

import net.dv8tion.jda.api.entities.{Message, PrivateChannel, User}
import org.joda.time.{DateTime, Minutes}
import org.slf4j.LoggerFactory

case class SurveyBot(author: User, channel: PrivateChannel, announceChannel: Long) {
  import SurveyBot._
  private val Logger = LoggerFactory.getLogger(s"SurveyBot [${author.getAsTag}]")

  private val birthday = DateTime.now()

  private var state: SurveyBotState = StateUsername
  private var username: Option[String] = None
  private var country: Option[String] = None
  private var steamUrl: Option[String] = None
  private var fullSeason: Option[Boolean] = None
  private var thousandGames: Option[Boolean] = None
  private var placementKind: Option[String] = None

  private def sendMessageAsync(msg: String, onComplete: Message => Unit = (_: Message) => {}): Unit = {
    channel.sendMessage(msg).queue(msg => onComplete(msg))
  }

  private def sendAnnounce(): Unit = {
    Logger.info(s"Sending announce for ${author.getAsMention}")
    channel.getJDA.getTextChannelById(announceChannel).sendMessage(s"${author.getAsMention} has thrown their hat in the ring!").queue()
  }

  private def handleUsername(message: Message): Unit = {
    username = Some(message.getContentRaw)
    state = ConfirmUsername
    sendMessageAsync(s"Your username is `${message.getContentRaw}`. Is that correct? Please answer `y` or `n`")
  }

  private def handleUsernameConfirm(message: Message): Unit = {
    textToBoolean(message.getContentRaw) match {
      case Right(true) =>
        //username correct
        state = CountryResponse
        sendMessageAsync("Great. On the SCL website and in results, we can put your country's flag next to your name. If you want that, give me the 2-character " +
          "ISO country code of the country you want. If you don't want a country, type `none`. A list of 2-character codes can be found at <https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#Officially_assigned_code_elements>")
      case Right(false) =>
        state = StateUsername
        sendMessageAsync("No worries. Please tell me your exact in-game username. Include `/Steam` if applicable")
      case Left(error) =>
        sendMessageAsync(error)
    }
  }

  private def handleCountry(message: Message): Unit = {
    if (message.getContentRaw.toLowerCase == "none") {
      state = LastSeasonAsked
      sendMessageAsync("No worries. You won't have a flag. Next question: did you play the FULL season for SCL 5? Please answer `y` or `n`")
    } else {
      textToCountry(message.getContentRaw) match {
        case Right(c) =>
          country = Some(c)
          state = LastSeasonAsked
          sendMessageAsync(s"Now you'll be representing :flag_$c: in SCL! Next question: did you play the FULL season for SCL 5? Please answer `y` or `n`")
        case Left(_) => sendMessageAsync("I don't know that country code. Please enter a two character country code (can be found at <https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#Officially_assigned_code_elements> or `none` if you don't want a flag.")
      }
    }

  }

  private def handleLastSeasonAnswer(message: Message): Unit = {
    textToBoolean(message.getContentRaw) match {
      case Right(true) =>
        state = StateToDie
        fullSeason = Some(true)
        logEverything()
        sendAnnounce()
        sendMessageAsync("Cool. We've got everything we need. Thanks and good luck in SCL 6!")
      case Right(false) =>
        state = ThousandGamesAsked
        fullSeason = Some(false)
        sendMessageAsync("Cool, now I have to ask: Do you CURRENTLY have 1,000 or more games played? Please answer `y` or `n`")
      case Left(error) =>
        sendMessageAsync(error)
    }
  }

  private def handleThousandGames(message: Message): Unit = {
    textToBoolean(message.getContentRaw) match {
      case Right(true) =>
        state = PlacementMethodAsked
        thousandGames = Some(true)
        sendMessageAsync("Awesome! One final question: how do you want to be placed? Answer `1` if you want to be in the lowest available division. Answer `2` if you want to attempt a higher placement.")
      case Right(false) =>
        state = StateToDie
        sendAnnounce()
        thousandGames = Some(false)
        logEverything()
        sendMessageAsync("Not a problem, SCL is available to players of all skill levels. We've got everything we need from you for now. Thanks for signing up and good luck!")
      case Left(error) =>
        sendMessageAsync(error)
    }
  }

  private def checkWodarAllowedCountries(countryCode: String): Either[String, String] = ???

  private def handleSteamLink(message: Message): Unit = {
    message.getContentRaw match {
      case SteamUrlRegex(_) =>
        state = ThousandGamesAsked
        steamUrl = Some(message.getContentRaw)
        sendMessageAsync("Cool. Next question: did you play the FULL season for SCL 5? Please answer `y` or `n`")
      case _                =>
        sendMessageAsync(s"I don't recognize that steam URL. Try again")
    }
  }

  private def handlePlacementMethod(message: Message): Unit = {
    textToOneOrTwo(message.getContentRaw) match {
      case Right(1) =>
        state = StateToDie
        placementKind = Some("Lowest")
        logEverything()
        sendAnnounce()
        sendMessageAsync("Awesome. That's all we need for now. Thanks for signing up and good luck!")
      case Right(2) =>
        state = StateToDie
        placementKind = Some("Gauntlet")
        logEverything()
        sendAnnounce()
        sendMessageAsync("Awesome. That's all we need for now. You will be contacted about placement matches. Thanks for signing up and good luck!")
      case Right(_) => require(false)
      case Left(error) =>
        sendMessageAsync(error)
    }
  }

  private def handleToDie(message: Message): Unit = {
    sendMessageAsync("I'm done! I don't need anything else from you!")
  }

  private def logEverything(): Unit = {
    Logger.info(s"Here we should persist the following: ${author.getAsTag} (${author.getId}) has signed up. " +
      s"Username = $username Country = $country Full season = $fullSeason. Thousand = $thousandGames Placement = $placementKind")
  }

  def readyForCleanup: Boolean = state == StateToDie || Minutes.minutesBetween(birthday, DateTime.now()).getMinutes >= SurveyLifetimeMinutes
  def cleanup(): Unit = {
    Logger.info("Cleaning up channel")
    channel.close().queue()
  }

  def gotMessage(msg: Message): Unit = {
    Logger.info(s"Got message ${msg.getContentRaw}")
    state match {
      case StateUsername        => handleUsername(msg)
      case ConfirmUsername      => handleUsernameConfirm(msg)
      case CountryResponse      => handleCountry(msg)
      case LastSeasonAsked      => handleLastSeasonAnswer(msg)
      case ThousandGamesAsked   => handleThousandGames(msg)
      case PlacementMethodAsked => handlePlacementMethod(msg)
      case StateToDie           => handleToDie(msg)
    }
  }

  Logger.info(s"Sent opening message")
  sendMessageAsync("Hello there! I see you want to join SCL. Just a couple questions to ask and we'll get you sorted.")
  sendMessageAsync("First up: Please type your username **EXACTLY** as it appears in the game. Include `/Steam` if applicable")

}

object SurveyBot {
  private val YesWords = Set("yes", "y", "true")
  private val NoWords = Set("no", "n", "false")
  private val SurveyLifetimeMinutes = 10
  private val Countries = Locale.getISOCountries.map(_.toLowerCase).toSet
  private val SteamUrlRegex = """https://steamcommunity.com/profiles/([0-9]*)""".r

  private[discord] def textToCountry(text: String): Either[String, String] = {
    if (Countries.contains(text)) {
      Right(text)
    } else {
      Left(s"I don't have a country code for $text")
    }
  }

  private[discord] def textToBoolean(text: String): Either[String, Boolean] = {
    val normalizedText = text.toLowerCase
    if (YesWords.contains(normalizedText)) {
      Right(true)
    } else if (NoWords.contains(normalizedText)) {
      Right(false)
    } else {
      Left(s"I don't know what `$text` means. Please use `y` or `n`")
    }
  }

  private[discord] def textToOneOrTwo(text: String): Either[String, Int] = {
    text match {
      case "1" => Right(1)
      case "2" => Right(2)
      case _   => Left("Sorry. Please answer `1` or `2`")
    }
  }
}

sealed trait SurveyBotState
case object StateUsername extends SurveyBotState
case object ConfirmUsername extends SurveyBotState
case object CountryResponse extends SurveyBotState
case object LastSeasonAsked extends SurveyBotState
case object ThousandGamesAsked extends SurveyBotState
case object PlacementMethodAsked extends SurveyBotState
case object StateToDie extends SurveyBotState
