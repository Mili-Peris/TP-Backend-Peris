package controllers.requests

import play.api.libs.json._

case class RemainingTicketsRequest(remaining: Long, showId: Long, zoneId: Long, performanceId: Long)

object RemainingTicketsRequest {
  implicit val read: play.api.libs.json.OFormat[RemainingTicketsRequest] =
    Json.format[RemainingTicketsRequest]
}
