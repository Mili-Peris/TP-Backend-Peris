package databases.tables

import databases.entities.Performance
import slick.lifted.{ProvenShape, Tag}
import slick.jdbc.PostgresProfile.api._

import java.time.LocalDate

class PerformancesTable(tag: Tag) extends Table[Performance](tag, "performances"){
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def date = column[LocalDate]("date_performance")

  def active = column[Boolean]("active_performance")

  def showId = column[Long]("id_show")

  def showFk = foreignKey("performances_show_fk", showId, TableQuery[ShowsTable])(_.id)

  override def * : ProvenShape[Performance] = (id, date, active, showId) <> (Performance.tupled, Performance.unapply)
}
