package databases.tables
import databases.entities.Booking
import slick.lifted.{ProvenShape, Tag}
import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlProfile.ColumnOption.SqlType

class BookingsTable(tag: Tag) extends Table[Booking](tag, "bookings"){
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def amount = column[Long]("amount_booking")

  def userId = column[Long]("id_user")

  def userFk = foreignKey("bookings_user_fk", userId, TableQuery[UsersTable])(_.id)

  def performanceId = column[Long]("id_performance")

  def performanceFk = foreignKey("bookings_performance_fk", performanceId, TableQuery[PerformancesTable])(_.id)

  def zoneId = column[Long]("id_zone")

  def zoneFk = foreignKey("bookings_zone_fk", zoneId, TableQuery[ZonesTable])(_.id)

  override def * : ProvenShape[Booking] = (id, amount, userId, performanceId, zoneId) <> (Booking.tupled, Booking.unapply)
}
