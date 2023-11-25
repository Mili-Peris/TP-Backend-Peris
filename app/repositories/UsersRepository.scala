package repositories

import controllers.requests.{UserRequestFund, UsersRequest}
import databases.entities.User
import databases.tables.UsersTable
import exceptions.UserNotFoundException
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global

class UsersRepository {

  private val usersTable = TableQuery[UsersTable]

  def getAllUsers = usersTable.result

  def getUserById(userId: Long) = {
    val findUser = usersTable.filter(_.id === userId).forUpdate.result.headOption.flatMap {
      case Some(value) => DBIO.successful(value)
      case None => DBIO.failed(UserNotFoundException("user not found"))
    }
    findUser
  }

  def addUser(userReq: UsersRequest, balance: Long) = {
    val newUser = User(Int.MinValue, userReq.name, balance)
    usersTable returning usersTable += newUser
  }

  def fundUser(user: User, userReqAcredit: UserRequestFund) = {
    val updateBalance = usersTable.filter(_.id === user.id).map(_.balance)
    updateBalance.update(userReqAcredit.amount + user.balance)
  }

  def updateUserBalance(user: User, amount: BigDecimal ) = {
    val updatedBalance = usersTable.filter(_.id === user.id).map(_.balance).update(user.balance - amount)
    updatedBalance
  }
}