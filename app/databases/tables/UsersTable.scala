package databases.tables

import databases.entities.User
import slick.lifted.{ProvenShape, Tag}
import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlProfile.ColumnOption.SqlType

class UsersTable (tag: Tag) extends Table[User](tag, "users"){
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name_user")

  def balance = column[BigDecimal]("balance_user", SqlType("numeric(10,2)"))

  override def * : ProvenShape[User] = (id, name, balance) <> (User.tupled, User.unapply)

}
