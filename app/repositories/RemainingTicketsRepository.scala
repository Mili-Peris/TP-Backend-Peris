package repositories
import databases.entities.{Performance, RemainingTickets, Zone}
import databases.tables.RemainingTicketsTable
import exceptions.TicketNotFoundException
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global

class RemainingTicketsRepository {

  private val ticketsTable = TableQuery[RemainingTicketsTable]

  def addTicket(performance: Performance, zone: Zone) = {
    val newTicket = RemainingTickets(Int.MinValue, zone.capacity, zone.showId, zone.id, performance.id)
    ticketsTable returning ticketsTable += newTicket
  }

  def getTicketById(showId: Long, zoneId: Long, performanceId: Long) = {
   val findById= ticketsTable.filter(remaining => remaining.showId === showId && remaining.zoneId === zoneId && remaining.performanceId === performanceId).forUpdate.result.headOption.flatMap {
      case Some(value) => DBIO.successful(value)
      case None => DBIO.failed(TicketNotFoundException("Ticket id not found"))
    }
    findById
  }

  def updateTicket(ticket: RemainingTickets, amount: Long) = {
    val updatedTicket = ticketsTable.filter(_.id === ticket.id).map(_.remaining).update(ticket.remaining - amount)
    updatedTicket
  }

  def getTicketByPerformanceAndShowId(performanceId: Long, showId: Long) = {
   ticketsTable.filter(remaining => remaining.performanceId === performanceId && remaining.showId === showId).result.headOption.flatMap{
     case Some(value) => DBIO.successful(value)
     case None => DBIO.failed(TicketNotFoundException("Ticket id not found"))
   }
  }
}
