package controllers
import controllers.requests.BookingsRequest
import databases.entities.Booking
import exceptions.{BookingNotFoundException, DatabaseServerError, PerformanceNotFoundException, TicketNotFoundException, UserNotFoundException, VenueNotFoundException, ZoneNotFoundException}
import play.api.libs.Files.logger
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.BookingsService
import wires.BookingWritter.bookingWrite

import javax.inject._

@Singleton
class BookingsController @Inject()(val controllerComponents: ControllerComponents, bookingsService: BookingsService) extends BaseController {

  def add(): Action[AnyContent] = Action { request =>
    val content: AnyContent = request.body
    val jsonObject: Option[JsValue] = content.asJson
    val bookingsItem: Option[BookingsRequest] = jsonObject.flatMap(Json.fromJson[BookingsRequest](_).asOpt)
    bookingsItem match {
      case Some(x) =>
        val result: Either[Throwable, Booking] = bookingsService.addBooking(x)
        result match {
          case Right(value) => Ok(Json.toJson(value))
          case Left(BookingNotFoundException(badRequestError)) => BadRequest(Json.obj("error" -> badRequestError))
          case Left(VenueNotFoundException(badRequestError)) => BadRequest(Json.obj("error" -> badRequestError))
          case Left(PerformanceNotFoundException(badRequestError)) => BadRequest(Json.obj("error" -> badRequestError))
          case Left(UserNotFoundException(badRequestError)) => BadRequest(Json.obj("error" -> badRequestError))
          case Left(ZoneNotFoundException(badRequestError)) => BadRequest(Json.obj("error" -> badRequestError))
          case Left(TicketNotFoundException(badRequestError)) => BadRequest(Json.obj("error" -> badRequestError))
          case Left(_) => InternalServerError(Json.obj("error"-> "errorFromServer"))
        }
      case None => BadRequest(Json.obj("error" -> "badRequestError"))
    }
  }

  def get(userId:Long): Action[AnyContent] = Action {
    val result = bookingsService.getAllBookingsByUserId(userId)
    result match {
      case Right(value) =>
        logger.info(s"El booking con userId: $userId fue mostrada")
        Ok(Json.toJson(value))
      case Left(_) =>
        logger.info("Problemas en la database")
        InternalServerError(Json.obj("error"-> "errorFromServer"))
    }
  }

}