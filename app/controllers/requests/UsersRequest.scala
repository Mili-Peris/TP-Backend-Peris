package controllers.requests

import play.api.libs.json._

case class UsersRequest(name: String)

object UsersRequest {
  implicit val read: play.api.libs.json.OFormat[UsersRequest] =
    Json.format[UsersRequest]
}

case class UserRequestFund(amount: Long)

object UserRequestFund {
  implicit val read: play.api.libs.json.OFormat[UserRequestFund] =
    Json.format[UserRequestFund]
}