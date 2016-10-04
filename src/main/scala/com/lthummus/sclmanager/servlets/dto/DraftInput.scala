package com.lthummus.sclmanager.servlets.dto

import org.json4s.DefaultFormats
import zzz.generated.tables.records.DraftRecord
import org.json4s.jackson.JsonMethods._

case class DraftInput(roomCode: String, player1: String, player2: String, payload: DraftPayload)

/*
            'room_id': self.id,
            'banned_maps': self.draft.serializable_bans(),
            'picked_maps': self.draft.serializable_picks(),
            'player_one': self.draft.player_one,
            'player_two': self.draft.player_two,
            'map_pool': self.serializable_map_pool(),
            'current_player': self.draft.current_player,
            'start_player': self.draft.start_player,
            'coin_flip_winner': self.draft.coin_flip_winner,
            'coin_flip_loser': self.draft.coin_flip_loser(),
            'first_spy': self.draft.first_spy,
            'state': self.draft.state,
            'user_readable_state': self.draft.user_readable_state(),
            'draft_type': self.draft_type.name
 */

case class DraftPayload(bannedMaps: List[Selection], pickedMaps: List[Selection], startPlayer: String, firstSpy: String)

object DraftPayload {
  implicit val formats = DefaultFormats

  def apply(json: String): DraftPayload = {
    parse(json).camelizeKeys.extract[DraftPayload]
  }
}

case class Selection(picker: String, map: String)