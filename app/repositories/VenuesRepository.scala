package repositories

import controllers.requests.VenuesRequest
import databases.entities.Venue
import databases.tables.VenuesTable
import exceptions.VenueNotFoundException
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery
import scala.concurrent.ExecutionContext.Implicits.global


class VenuesRepository {

  private val venuesTable = TableQuery[VenuesTable]

  def getAllVenues = venuesTable.result

  def getVenueById(venueId: Long) = {
    venuesTable.filter(_.id === venueId).result.headOption.flatMap {
      case Some(value) => DBIO.successful(value)
      case None => DBIO.failed(VenueNotFoundException("venue not found"))
    }
  }
  //para correr el db 1 sola vez

  def addVenue(venueBody: VenuesRequest) = {
    val newVenue = Venue(Int.MinValue, venueBody.name, venueBody.capacity)
    //venuesTable returning venuesTable.map(Venue => Venue.id) into ((Venue, id) => Venue.copy(id=id))
    //insertQuery += newVenue
    venuesTable returning venuesTable += newVenue
  }
  //cuando llamo al minimo valor como id luego se machea con el valor correspondiente


}
