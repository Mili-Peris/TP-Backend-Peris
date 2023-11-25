package controllers

import controllers.requests.{UserRequestFund, UsersRequest}
import databases.entities.User
import wires.UserWritter.userWritter
import exceptions.{DatabaseServerError, UserNotFoundException}
import play.api.libs.Files.logger
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.UsersService

import javax.inject._

@Singleton
class UsersController @Inject()(val controllerComponents: ControllerComponents, usersService: UsersService) extends BaseController {

  def add(): Action[AnyContent] = Action { request =>
    val content: AnyContent = request.body
    val jsonObject: Option[JsValue] = content.asJson
    val showUser: Option[UsersRequest] = jsonObject.flatMap(Json.fromJson[UsersRequest](_).asOpt)
    showUser match {
      case Some(x) =>
        val result: Either[Throwable, User] = usersService.addUser(x)
        result match {
          case Right(value) => Ok(Json.toJson(value))
          case Left(UserNotFoundException(badRequestError)) => BadRequest(Json.obj("error" -> badRequestError))
          case Left(_) => InternalServerError(Json.obj("error"-> "errorFromServer"))
        }
      case None => BadRequest(Json.obj("error" -> "badRequestError"))
    }
  }

  def getAll(): Action[AnyContent] = Action {
    val result = usersService.getAllUsers()
    result match {
      case Right(value) =>
        logger.info("Todos los usuarios fueron mostradas")
        Ok(Json.toJson(value))
      case Left(_) =>
        logger.info("Probelemas en la database")
        InternalServerError(Json.obj("error"-> "errorFromServer"))
    }
  }

  def get(userId: Long): Action[AnyContent] = Action {
    val result = usersService.getUser(userId)
    result match {
      case Right(value) =>
        logger.info(s"La user con userId: $userId fue mostrada")
        Ok(Json.toJson(value))
      case Left(UserNotFoundException(badRequestError)) =>
        BadRequest(Json.obj("error" -> badRequestError))
      case Left(_) =>
        logger.info("Probelemas en la database")
        InternalServerError(Json.obj("error"-> "errorFromServer"))
    }
  }

  def fund(userId: Long): Action[AnyContent] = Action { request =>
    val content: AnyContent = request.body
    val jsonObject: Option[JsValue] = content.asJson
    val foundUser: Option[UserRequestFund] = jsonObject.flatMap(Json.fromJson[UserRequestFund](_).asOpt)
    foundUser match {
      case Some(x) =>
        val result: Either[Throwable, User] = usersService.fundUser(userId,x)
        result match {
          case Right(value) => Ok(Json.toJson(value))
          case Left(UserNotFoundException(badRequestError)) => BadRequest(Json.obj("error" -> badRequestError))
          case Left(_) => InternalServerError(Json.obj("error"-> "errorFromServer"))
        }
      case None => BadRequest(Json.obj("error" -> "badRequestError"))
    }
  }

}
