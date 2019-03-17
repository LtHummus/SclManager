package com.lthummus.sclmanager.servlets.dto

import com.lthummus.sclmanager.parsing._
import com.lthummus.sclmanager.scaffolding.SclManagerConfig
import org.joda.time.DateTime
import org.jooq.Record
import scalaz._
import zzz.generated.Tables
import zzz.generated.tables.records.{BoutRecord, DivisionRecord, GameRecord, PlayerRecord}

case class Match(id: Int,
                 week: Int,
                 league: String,
                 player1: SimplePlayer,
                 player2: SimplePlayer,
                 status: Int,
                 matchType: String,
                 games: Option[List[Game]],
                 matchUrl: Option[String],
                 draft: Option[Draft],
                 draftSummary: Option[String],
                 scoreSummary: Option[String],
                 summary: Option[List[String]] = None,
                 forumPost: Option[String] = None,
                 discordPost: Option[String] = None,
                 forfeitWinner: Option[String] = None,
                 forfeitText: Option[String] = None) {

}

case class Game(id: Int,
                spy: String,
                sniper: String,
                matchId: Int,
                result: String,
                level: String,
                gameType: String,
                sequence: Int,
                startDurationSeconds: Option[Int],
                guests: Option[Int],
                uuid: String,
                timestamp: DateTime) {

  def isCompleted: Boolean = result != GameResultEnum.InProgress.niceName
  def spyWon: Boolean = result == GameResultEnum.CivilianShot.niceName || result == GameResultEnum.MissionWin.niceName
  def sniperWon: Boolean = result == GameResultEnum.SpyShot.niceName || result == GameResultEnum.SpyTimeout.niceName

  val winnerName: String = if (spyWon) spy else sniper
  val winnerRole: String = if (spyWon) "spy" else "sniper"

  val description: String = s"$winnerName won as $winnerRole on $level $gameType"

  def asReplay: Replay = {
    val disjointReplay = for {
      parsedResult <- GameResultEnum.fromString(result)
      parsedLevel <- Level.getLevelByName(level)
      parsedGameType <- GameType.fromString(gameType)
    } yield Replay(spy, sniper, timestamp, parsedResult, parsedLevel, parsedGameType, sequence, uuid, -1, startDurationSeconds, guests)

    disjointReplay match {
      case -\/(s) => throw new Exception(s"Unable to parse match: $s")
      case \/-(replay) => replay
    }
  }
}

case class MatchList(matches: Seq[Match])

object Match {
  def fromDatabaseRecordWithGames(record: Record, games: List[GameRecord], playerMap: Map[String, Player], draft: Option[Draft]): Match = {
    val boutRecord: BoutRecord = record.into(Tables.BOUT)
    val divisionRecord: DivisionRecord = record.into(Tables.DIVISION)
    val gameList = games.map(Game.fromDatabaseRecord)
    val packagedMatchUrl = Option(boutRecord.getMatchUrl)

    val maybeBout = gameList.size match {
      case 0 => None
      case _ => Some(Bout(gameList.map(_.asReplay), BoutTypeEnum.fromInt(boutRecord.getBoutType)))
    }

    val draftSummary = draft.map(_.asForumPost)

    val forumPost = maybeBout.map(bout =>
      s"""Results for ${bout.player1} v. ${bout.player2} (Week ${boutRecord.getWeek} - ${boutRecord.getDivision})<br />
        |<br />
        |${draftSummary.getOrElse("Draft Data Unavailable")}<br />
        |<br />
        |[results]<br />
        |${bout.getScoreLine}<br />
        |<br />
        |${bout.getForumGameSummary}<br />
        |[/results]<br />
        |<br />
        |Download link: [url]${SclManagerConfig.serverHost}/download/${boutRecord.getId}[/url]<br />""".stripMargin.lines.mkString(""))

    //discord makes us put the IDs, for reasons
    val emojiId = boutRecord.getDivision match {
      case "Silver"     => "<:scl_badge_silver:545019757401997323>"
      case "Platinum"   => "<:scl_badge_platinum:545019874464890901>"
      case "Obsidian"   => "<:scl_badge_obsidian:551458388765048833>"
      case "Oak"        => "<:scl_badge_oak:545016068905631759>"
      case "Iron"       => "<:scl_badge_iron:545019816688222228>"
      case "Gold"       => "<:scl_badge_gold:545018456169512960>"
      case "Diamond"    => "<:scl_badge_diamond:545019694827044883>"
      case "Copper"     => "<:scl_badge_copper:545019911081295891>"
      case "Bronze"     => "<:scl_badge_bronze:545019772161622031>"
      case "Bamboo"     => "<:scl_badge_bamboo:545017748288176129>"
      case "Challenger" => "<:scl_badge_challenger:545019926021144578>"
      case _            => boutRecord.getDivision
    }

    val player1FlagEmoji = Option(playerMap(boutRecord.getPlayer1).country) match {
      case Some(x) if x.length == 2 => s":flag_$x: "
      case _                        => ""
    }

    val player2FlagEmoji = Option(playerMap(boutRecord.getPlayer2).country) match {
      case Some(x) if x.length == 2 => s":flag_$x: "
      case _                        => ""
    }

    val discordPost = maybeBout.map(bout =>
      s"""**Results for $player1FlagEmoji${boutRecord.getPlayer1} v. $player2FlagEmoji${boutRecord.getPlayer2} (Week ${boutRecord.getWeek} - $emojiId)**
         |${draft.map(_.asDiscordPost).getOrElse("Draft Data Unavailable :sweat:")}
         |||${bout.getScoreLine}
         |
         |${bout.getDiscordGameSummary}|||Download link: ${SclManagerConfig.serverHost}/download/${boutRecord.getId}""".stripMargin.lines.mkString("\n")
    )

    def calculateForfeitWinner: Option[String] = {
      (boutRecord.getStatus.toInt, Option(boutRecord.getForfeitWinner)) match {
        case (2, None)         => Some("Double forfeit. No one ")
        case (2, Some(winner)) => Some(winner)
        case _                 => None
      }
    }


    if (divisionRecord.getSecret) {
      Match(boutRecord.getId,
        boutRecord.getWeek,
        boutRecord.getDivision,
        playerMap(boutRecord.getPlayer1).asSimplePlayer,
        playerMap(boutRecord.getPlayer2).asSimplePlayer,
        boutRecord.getStatus,
        BoutTypeEnum.fromInt(boutRecord.getBoutType).toString,
        None,
        packagedMatchUrl,
        draft,
        draftSummary,
        None,
        None,
        None,
        None,
        calculateForfeitWinner,
        Option(boutRecord.getForfeitText)
      )
    } else {
      Match(boutRecord.getId,
        boutRecord.getWeek,
        boutRecord.getDivision,
        playerMap(boutRecord.getPlayer1).asSimplePlayer,
        playerMap(boutRecord.getPlayer2).asSimplePlayer,
        boutRecord.getStatus,
        BoutTypeEnum.fromInt(boutRecord.getBoutType).toString,
        Some(gameList),
        packagedMatchUrl,
        draft,
        draftSummary,
        maybeBout.map(_.getScoreLine),
        maybeBout.map(_.getGameSummary),
        forumPost,
        discordPost,
        calculateForfeitWinner,
        Option(boutRecord.getForfeitText))
      }
    }

}

object Game {
  def fromDatabaseRecord(record: GameRecord): Game = {
    val gameResult = GameResultEnum.fromInt(record.getResult) match {
      case -\/(error) => error
      case \/-(res) => res.toString
    }

    val recordId = if (record.getId == null) new Integer(-1) else record.getId

    Game(recordId,
      record.getSpy,
      record.getSniper,
      record.getBout,
      gameResult,
      record.getVenue,
      record.getGametype,
      record.getSequence,
      Option(record.getStartDurationSeconds).map(_.toInt),
      Option(record.getGuests).map(_.toInt),
      record.getUuid,
      new DateTime(record.getTimestamp))
  }
}