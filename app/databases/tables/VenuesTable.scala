package databases.tables

import databases.entities.Venue
import slick.lifted.{ProvenShape, Tag}
import slick.jdbc.PostgresProfile.api._

class VenuesTable(tag: Tag) extends Table[Venue](tag, "venues") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name_venue")

  def capacity = column[Long]("capacity_venue")

  override def * : ProvenShape[Venue] = (id, name, capacity) <> (Venue.tupled, Venue.unapply)
}
