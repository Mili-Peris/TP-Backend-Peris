package services

import com.google.inject.{Inject, Singleton}
import controllers.requests.{UserRequestFund, UsersRequest}
import databases.entities.User
import exceptions.UserNotFoundException
import repositories._
import slick.jdbc.JdbcBackend.Database
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util._

@Singleton
class UsersService @Inject()(
                            usersRepository: UsersRepository
                            ) {
  private val db = Database.forConfig("postgres")

    def validateReq(usersReq: UsersRequest) = {
      usersReq match {
        case k if usersReq.name.isEmpty => Left(UserNotFoundException("name is empty"))
        case _ => Right(())
      }
    }

  def validateFundReq(valFundReq: UserRequestFund) = {
    valFundReq match {
      case k if valFundReq.amount <= 0 => Left(UserNotFoundException("amount not valid"))
      case _ => Right(())
    }
  }

    def addUser(usersReq: UsersRequest): Either[Throwable, User] = {
      for {
        _ <- validateReq(usersReq)
        res <- Try(Await.result(db.run(usersRepository.addUser(usersReq, 0)), 10.seconds)).toEither
      } yield res
    }

    def getAllUsers() = {
      for {
        result <- Try(Await.result(db.run(usersRepository.getAllUsers), 10.seconds)).toEither
      } yield result
    }

    def getUser(userId: Long) = {
      for {
        result <- Try(Await.result(db.run(usersRepository.getUserById(userId)), 10.seconds)).toEither
      } yield result
    }

    def fundUser(userId: Long, userRequestFund: UserRequestFund) = {
      validateFundReq(userRequestFund).flatMap { _ =>
        val fundUs =
          for {
            userPast <- usersRepository.getUserById(userId)
            _ <- usersRepository.fundUser(userPast, userRequestFund)
            user <- usersRepository.getUserById(userId)
          } yield user
        Try(Await.result(db.run(fundUs), 10.seconds)).toEither
      }
    }

}
