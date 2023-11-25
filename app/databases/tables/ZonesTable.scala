package databases.tables

import databases.entities.Zone
import slick.lifted.{ProvenShape, Tag}
import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlProfile.ColumnOption.SqlType

class ZonesTable(tag: Tag) extends Table[Zone](tag, "zones") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name_zone")

  def price = column[BigDecimal]("price_zone", SqlType("numeric(10,2)"))

  def capacity = column[Long]("capacity_zone")

  def showId = column[Long]("id_show")

  def showFk = foreignKey("zones_show_fk", showId, TableQuery[ShowsTable])(_.id)

  override def * : ProvenShape[Zone] = (id, name, capacity,price, showId) <> (Zone.tupled, Zone.unapply)
}
