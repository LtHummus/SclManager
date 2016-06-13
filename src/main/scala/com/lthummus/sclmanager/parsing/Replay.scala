package com.lthummus.sclmanager.parsing

import com.lthummus.sclmanager.parsing.GameLoadoutType.GameLoadoutType
import com.lthummus.sclmanager.parsing.GameResult.GameResult

object GameResult extends Enumeration {
  type GameResult = Value
  val MissionWin, SpyTimeout, SpyShot, CivilianShot, InProgress = Value
}

object GameLoadoutType extends Enumeration {
  type GameLoadoutType = Value
  val Known, Any, Pick = Value
}

case class GameType(kind: GameLoadoutType, x: Int, y: Int) {
  override def toString = kind match {
    case GameLoadoutType.Known => s"k$x"
    case GameLoadoutType.Any => s"a$x/$y"
    case GameLoadoutType.Pick => s"p$x/$y"
  }
}

case class Replay(spy: String, sniper: String, duration: Float, gameUuid: String, startTime: Int, result: GameResult, level: Level, loadoutType: GameType) extends Ordered[Replay] {
  override def compare(that: Replay): Int = this.startTime - that.startTime
}

object Replay {
  def fromBytes(bytes: Array[Byte]) = {

  }
}