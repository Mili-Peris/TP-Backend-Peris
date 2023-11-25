package repositories

import controllers.requests.ZonesRequest
import databases.entities.Zone
import databases.tables.ZonesTable
import exceptions.ZoneNotFoundException
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery
import scala.concurrent.ExecutionContext.Implicits.global

class ZonesRepository {

  private val zonesTable = TableQuery[ZonesTable]

  def getZoneByShowId(showId: Long) = {
    zonesTable.filter(p => p.showId === showId).result
  }

  def getZoneById(zoneId: Long) ={
    zonesTable.filter(_.id === zoneId).result.headOption.flatMap {
      case Some(x) => DBIO.successful(x)
      case None => DBIO.failed(ZoneNotFoundException("zone id not found"))
    }
  }

  def addZone(zonesRequest: ZonesRequest, showId:Long) = {
    val newZone = Zone(Int.MinValue, zonesRequest.name, zonesRequest.capacity, zonesRequest.price, showId)
    zonesTable returning zonesTable += newZone
  }

}
