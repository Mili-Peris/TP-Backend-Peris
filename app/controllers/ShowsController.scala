package controllers
import controllers.requests.{PauseRequest, ShowsRequest}
import wires.ShowWritter.showWrite
import wires.PerformanceWritter.performanceWritter
import databases.entities.{Performance, Show}
import exceptions.{PerformanceNotFoundException, ShowNotFoundException, VenueNotFoundException}
import play.api.libs.Files.logger
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.ShowsService

import javax.inject._

@Singleton
class ShowsController @Inject()(val controllerComponents: ControllerComponents, showsService: ShowsService) extends BaseController {

  def add(): Action[AnyContent] = Action { request =>
    val content: AnyContent = request.body
    val jsonObject: Option[JsValue] = content.asJson
    val showItem: Option[ShowsRequest] = jsonObject.flatMap(Json.fromJson[ShowsRequest](_).asOpt)
    showItem match {
      case Some(x) =>
        val result: Either[Throwable, Show] = showsService.addShow(x)
        result match {
          case Right(value) => Ok(Json.toJson(value))
          case Left(ShowNotFoundException(badRequestError)) => BadRequest(Json.obj("error" -> badRequestError))
          case Left(VenueNotFoundException(badRequestError)) => BadRequest(Json.obj("error" -> badRequestError))
          case Left(_) => InternalServerError(Json.obj("error"-> "errorFromServer"))
        }
      case None => BadRequest(Json.obj("error" -> "badRequestError"))
    }
  }

  def getAll(): Action[AnyContent] = Action {
    val result = showsService.getAllShows
    result match {
      case Right(value) =>
        logger.info("Todos las venues fueron mostradas")
        Ok(Json.toJson(value))
      case Left(_) =>
        logger.info("Probelemas en la database")
        InternalServerError(Json.obj("error"-> "errorFromServer"))
    }
  }

  def get(showId: Long): Action[AnyContent] = Action {
    val result = showsService.getShowById(showId)
    result match {
      case Right(value) =>
        logger.info(s"La venue con showId: $showId fue mostrada")
        Ok(Json.toJson(value))
      case Left(ShowNotFoundException(badRequestError)) =>
        BadRequest(Json.obj("error" -> badRequestError))
      case Left(_) =>
        logger.info("Probelemas en la database")
        InternalServerError(Json.obj("error" -> "errorFromServer"))
    }
  }

  def togglePause(showId: Long): Action[AnyContent] = Action { request =>
    val content: AnyContent = request.body
    val jsonObject: Option[JsValue] = content.asJson
    val pauseItem: Option[PauseRequest] = jsonObject.flatMap(Json.fromJson[PauseRequest](_).asOpt)
    pauseItem match {
      case Some(x) =>
        val result: Either[Throwable, Performance] = showsService.pauseShow(showId, x)
        result match {
          case Right(value) => Ok(Json.toJson(value))
          case Left(PerformanceNotFoundException(badRequestError)) => BadRequest(Json.obj("error" -> badRequestError))
          case Left(ShowNotFoundException(badRequestError)) => BadRequest(Json.obj("error" -> badRequestError))
          case Left(_) => InternalServerError(Json.obj("error"-> "errorFromServer"))
        }
      case None => BadRequest(Json.obj("error" -> "badRequestError"))
    }
  }

}
