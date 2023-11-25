package controllers.requests

import databases.entities.{Performance, RemainingTickets, Show, Zone}

import java.time.LocalDate
import play.api.libs.json.{Json, OFormat, _}

case class ShowsRequest(name: String, category: String, venueId: Long, performancesDates: Seq[LocalDate], zones: Seq[ZonesRequest])
object ShowsRequest {
  implicit val zoneRequestReads: play.api.libs.json.OFormat[ZonesRequest] = Json.format[ZonesRequest]
  implicit val readsShow: play.api.libs.json.OFormat[ShowsRequest] = Json.format[ShowsRequest]
}

case class PauseRequest(performanceId: Long, performanceState: Boolean)

object PauseRequest {
  implicit val read: play.api.libs.json.OFormat[PauseRequest] =
    Json.format[PauseRequest]
}

case class ShowResponseRemainings(show: Show, performances: Seq[Performance], zones: Seq[Zone], remainings: Seq[RemainingTickets], soldOut: Boolean)
object ShowResponseRemainings {
  implicit val ticketWrites: Writes[RemainingTickets] = Json.writes[RemainingTickets]
  implicit val showWrites: Writes[Show] = Json.writes[Show]
  implicit val performanceWrites: Writes[Performance] = Json.writes[Performance]
  implicit val zoneWrites: Writes[Zone] = Json.writes[Zone]
  implicit val showResponseRemainingsWrites: Writes[ShowResponseRemainings] = Json.writes[ShowResponseRemainings]

}

case class ShowSoldOut(showId: Long, showName: String, venueId: Long, showCategory: String, showPerformances: Seq[Performance], showZones: Seq[Zone], showRemainigs: Seq[RemainingTickets])
object ShowSoldOut {
  implicit val performanceWrites: Writes[Performance] = Json.writes[Performance]
  implicit val zoneWrites: Writes[Zone] = Json.writes[Zone]
  implicit val ticketWrites: Writes[RemainingTickets] = Json.writes[RemainingTickets]
  implicit val showSoldOutWrites: Writes[ShowSoldOut] = Json.writes[ShowSoldOut]
}






