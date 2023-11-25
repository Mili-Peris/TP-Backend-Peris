package controllers.requests

import play.api.libs.json._

case class BookingsRequest(amount: Long, userId: Long, performanceId: Long, zoneId: Long)
//amount: cantidad de entradas

object BookingsRequest {
  implicit val reads: play.api.libs.json.OFormat[BookingsRequest] = Json.format[BookingsRequest]
}