package controllers.requests

import play.api.libs.json._

case class ZonesRequest(name: String, capacity: Long, price: BigDecimal)

object ZonesRequest {
  implicit val read: play.api.libs.json.OFormat[ZonesRequest] =
    Json.format[ZonesRequest]
}