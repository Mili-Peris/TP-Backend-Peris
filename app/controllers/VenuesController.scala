package controllers

import controllers.requests.VenuesRequest
import databases.entities.Venue
import exceptions.{DatabaseServerError, VenueNotFoundException}
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.VenuesService
import wires.VenueWritter.venueWrite
import play.api.libs.Files.logger

import javax.inject._

@Singleton
class VenuesController @Inject()(val controllerComponents: ControllerComponents, venuesService: VenuesService) extends BaseController {

  def add(): Action[AnyContent] = Action {  request =>
    val content: AnyContent = request.body
    val jsonObject: Option[JsValue] = content.asJson
    val venueItem: Option[VenuesRequest] = jsonObject.flatMap(Json.fromJson[VenuesRequest](_).asOpt)
    venueItem match {
      case Some(x) =>
        val result: Either[Throwable, Venue] = venuesService.add(x)
        result match {
          case Right(value) => Ok(Json.toJson(value))
          case Left(VenueNotFoundException(badRequestError)) => BadRequest(Json.obj("error" -> badRequestError))
          case Left(_) => InternalServerError(Json.obj("error"-> "errorFromServer"))
        }
      case None => BadRequest(Json.obj("error" -> "badRequestError"))
    }
  }
/*
OTRA FORMA: ELEGIR CUAL ME GUSTA MAS
def createVenue2 = Action(parse.json) { request =>
    request.body.validate[VenueRequest]
    match {
      case JsSuccess(venue, _) =>
        venueService.createVenue(venue).map {
          case Right(id) =>
            logger.info(s"Venue with ID $id created successfully.")
            Ok(
              Json.obj(
                "message" -> s"Venue created successfully with ID $id",
                "id" -> id
              )
            )
          case Left(error) => handleError(error) // Aquí se maneja el error
        }
      case JsError(errors) =>
        logger.warn("Invalid json format received.")
        BadRequest(Json.obj("error" -> "Invalid json format"))
    }
}
 */
  def getAll(): Action[AnyContent] = Action {
    val result = venuesService.getAllVenues
    result match {
      case Right(x) =>
        logger.info("Todos las venues fueron mostradas")
        Ok(Json.toJson(x))
      case Left(VenueNotFoundException(badRequestError)) =>
        BadRequest(Json.obj("error" -> badRequestError))
      case Left(_)  =>
        logger.info("Probelemas en la database")
        InternalServerError(Json.obj("error"-> "errorFromServer"))
    }
  }

}