package controllers.requests

import java.time.LocalDate
import play.api.libs.json._

case class PerformancesRequest (active: Boolean, showId: Long, date: LocalDate)

object PerformancesRequest {
  implicit val readsShow: play.api.libs.json.OFormat[PerformancesRequest] = Json.format[PerformancesRequest]
}