package controllers.requests

import play.api.libs.json._

case class VenuesRequest(name: String, capacity: Long)

object VenuesRequest {
  implicit val readsVenue: play.api.libs.json.OFormat[VenuesRequest] = Json.format[VenuesRequest]
}
