package repositories

import controllers.requests.BookingsRequest
import databases.entities.Booking
import databases.tables.BookingsTable
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

class BookingsRepository {

  private val bookingsTable = TableQuery[BookingsTable]

  def getAllBookings = bookingsTable.result

  def getBookingsByUserId(userId: Long) = {
    bookingsTable.filter(_.userId === userId).result
  }


  def addBooking(bookingsRequest: BookingsRequest) = {
    val newBooking = Booking(Int.MinValue, bookingsRequest.amount, bookingsRequest.userId, bookingsRequest.performanceId, bookingsRequest.zoneId)
    bookingsTable returning bookingsTable += newBooking
  }
}