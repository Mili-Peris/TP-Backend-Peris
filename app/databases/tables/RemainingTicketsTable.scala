package databases.tables
import databases.entities.RemainingTickets

import slick.lifted.{ProvenShape, Tag}
import slick.jdbc.PostgresProfile.api._

class RemainingTicketsTable(tag: Tag) extends Table[RemainingTickets](tag, "tickets") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def remaining = column[Long]("remaining_tickets")

  def showId = column[Long]("id_show")

  def showFk = foreignKey("tickets_show_fk", showId, TableQuery[ShowsTable])(_.id)

  def zoneId = column[Long]("id_zone")

  def zoneFk = foreignKey("tickets_zone_fk", zoneId, TableQuery[ZonesTable])(_.id)

  def performanceId = column[Long]("id_performance")

  def performanceFk = foreignKey("tickets_performance_fk", performanceId, TableQuery[PerformancesTable])(_.id)

  override def * : ProvenShape[RemainingTickets] = (id, remaining, showId, zoneId, performanceId) <> (RemainingTickets.tupled, RemainingTickets.unapply)
}
