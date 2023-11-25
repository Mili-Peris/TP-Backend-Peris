package services
import com.google.inject.{Inject, Singleton}
import controllers.requests.BookingsRequest
import databases.entities._
import exceptions.{BookingNotFoundException, TicketNotFoundException}
import repositories._
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util._

@Singleton
class BookingsService @Inject()(
                              bookingsRepository: BookingsRepository,
                              usersRepository: UsersRepository,
                              performancesRepository: PerformancesRepository,
                              zonesRepository: ZonesRepository,
                              remainingTicketsRepository: RemainingTicketsRepository
  ) {

  private val db = Database.forConfig("postgres")

  def validateBookingReq(bookingReq: BookingsRequest): Either[Throwable, Unit] = {
    bookingReq match {
      case _ if bookingReq.amount < 0 || bookingReq.amount > 4 =>
        Left(BookingNotFoundException("invalid amount of tickets"))
      case _ => Right(())
    }
  }

  def userCanBuy(user: User, totalPrice: BigDecimal) = {
    user match {
      case User(_,_,balance) if balance < totalPrice =>
        DBIO.failed(TicketNotFoundException("insufficient balance"))
      case _ => DBIO.successful(())
    }

  }

  def performanceIsActive(performance: Performance) = {
    performance match {
      case Performance(_,_,false,_) =>
        DBIO.failed(TicketNotFoundException("performance is not available"))
      case _ => DBIO.successful(())
    }
  }

  def ticketsAreAvailable(ticket: RemainingTickets, amount: Long) = {
    ticket match {
      case RemainingTickets(_, ticketsRemaining, _, _, _) if (ticketsRemaining - amount) < 0 =>
        DBIO.failed(TicketNotFoundException("tickets are not available"))
      case _ => DBIO.successful(())
    }
  }

  def updateTickets(ticket: RemainingTickets, amount: Long) = {
    remainingTicketsRepository.updateTicket(ticket, amount)
  }

  def updateUserBalance(user: User, totalPrice: BigDecimal) = {
    usersRepository.updateUserBalance(user, totalPrice)
  }


  def addBooking(bookingReq: BookingsRequest): Either[Throwable, Booking] = {
    validateBookingReq(bookingReq).flatMap { _ =>
      val x = for {

        user <- usersRepository.getUserById(bookingReq.userId)
        zone <- zonesRepository.getZoneById(bookingReq.zoneId)
        performance <- performancesRepository.getPerformanceById(bookingReq.performanceId)
        ticket <- remainingTicketsRepository.getTicketById(zone.showId, zone.id, performance.id)


        totalPrice = zone.price * bookingReq.amount
        _ <- userCanBuy(user, totalPrice)
        _ <- ticketsAreAvailable(ticket, bookingReq.amount)
        _ <- performanceIsActive(performance)

        _ <- updateTickets(ticket, bookingReq.amount)
        _ <- updateUserBalance(user, totalPrice)

        booking <- bookingsRepository.addBooking(bookingReq)

      } yield booking

      Try(Await.result(db.run(x.transactionally), 10.seconds)).toEither
    }
  }


  def getAllBookingsByUserId(userId:Long) : Either[Throwable, Seq[Booking]] = {
    val x = for {
      _ <- usersRepository.getUserById(userId)
      bookings <- bookingsRepository.getBookingsByUserId(userId)
    } yield bookings

    Try(Await.result(db.run(x), 10.seconds)).toEither
  }

}

