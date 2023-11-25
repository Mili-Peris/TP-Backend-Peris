package wires

import controllers.requests.{ShowResponseRemainings}
import databases.entities.{Booking, Performance, RemainingTickets, Show, User, Venue, Zone}
import play.api.libs.json.{Json, OFormat, Writes}

object BookingWritter {
  implicit val bookingWrite: Writes[Booking] = Json.writes[Booking]
}

object PerformanceWritter {
  implicit val performanceWritter: Writes[Performance] = Json.writes[Performance]
}

object RemainingTicketsWritter {
  implicit val remainingTicketsWrite: Writes[RemainingTickets] = Json.writes[RemainingTickets]
}

object ShowWritter {
  implicit val showWrite: Writes[Show] = Json.writes[Show]
}

object UserWritter {
  implicit val userWritter: Writes[User] = Json.writes[User]
}

object VenueWritter {
  implicit val venueWrite: Writes[Venue] = Json.writes[Venue]
}

object ZoneWritter {
  implicit val zoneWrite: Writes[Zone] = Json.writes[Zone]
}


