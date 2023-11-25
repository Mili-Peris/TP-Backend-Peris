package databases.tables

import databases.entities.Show
import slick.lifted.{ProvenShape, Tag}
import slick.jdbc.PostgresProfile.api._

class ShowsTable(tag: Tag) extends Table[Show](tag, "shows") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name_show")

  def category = column[String]("category_show")

  def venueId = column[Long]("id_venue")

  def venueFk = foreignKey("shows_venue_fk", venueId, TableQuery[VenuesTable])(_.id)

  override def * : ProvenShape[Show] = (id, name, category, venueId) <> (Show.tupled, Show.unapply)
}
