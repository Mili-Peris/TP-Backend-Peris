package services

import com.google.inject.{Inject, Singleton}
import controllers.requests.{PauseRequest, ShowResponseRemainings, ShowsRequest, ZonesRequest, ShowSoldOut}
import databases.entities.{Performance, RemainingTickets, Show, Venue}
import exceptions.ShowNotFoundException
import repositories._
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._

import java.time.LocalDate
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util._
import repositories.RemainingTicketsRepository


@Singleton
class ShowsService @Inject()(
                              remainingsTicketsRepository: RemainingTicketsRepository,
                              showsRepository: ShowsRepository,
                              performancesRepository: PerformancesRepository,
                              zonesRepository: ZonesRepository,
                              venuesRepository: VenuesRepository,
                              remainingTicketsRepository: RemainingTicketsRepository
  ) {
  private val validCategories = List("Recital", "Musical", "Obra")
  private val validZones = List("Popular", "VIP", "Campo Delantero", "Campo Trasero")
  private val db = Database.forConfig("postgres")

  def validateZoneRequest(zoneReq: ZonesRequest): Either[Throwable, Unit] = {
    zoneReq match {
      case _ if zoneReq.name.isEmpty =>
        Left(ShowNotFoundException("Show zone name empty"))

      case _ if zoneReq.price <= 0 =>
        Left(ShowNotFoundException("Show zone ticket cost is non-natural"))

      case _ if zoneReq.price.scale > 2 =>
        Left(ShowNotFoundException("Show zone price has more than two decimals"))

      case _ if zoneReq.capacity <= 0 =>
        Left(ShowNotFoundException("Show zone capacity is non-natural"))
      case _ => Right(())
    }
  }


  def validateZoneCapacity(showRequest: ShowsRequest, venue: Venue) = {
    showRequest.zones.map { zone => zone.capacity }.sum match {
      case suma if suma > venue.capacity => DBIO.failed(ShowNotFoundException("Show zone capacities exceed venue capacity"))
      case _ => DBIO.successful(())
    }
  }

  def validateShowRequest(showReq: ShowsRequest): Either[Throwable, Unit] = {
    showReq match {
      case _ if showReq.name.isEmpty =>
        Left(ShowNotFoundException("Show name empty"))

      case _ if showReq.category.isEmpty =>
        Left(ShowNotFoundException("Show category empty"))

      case _ if !validCategories.contains(showReq.category) =>
        Left(ShowNotFoundException("Show category invalid"))

      case _ if showReq.performancesDates.isEmpty =>
        Left(ShowNotFoundException("No show performance dates given"))

      case _ if showReq.performancesDates.exists(date => date.isBefore(LocalDate.now())) =>
        Left(ShowNotFoundException("Show date(s) invalid"))

      case _ if showReq.zones.isEmpty =>
        Left(ShowNotFoundException("No show zones given"))

      case _ if showReq.zones.exists(zone => validateZoneRequest(zone).isLeft) =>
        showReq.zones.map(zone => validateZoneRequest(zone)).find(p => p.isLeft).get

      case _ => Right(())
    }
  }

  def addShow(showReq: ShowsRequest): Either[Throwable, Show] = {
    validateShowRequest(showReq).flatMap { _ =>
      val x = for {

        venue <- venuesRepository.getVenueById(showReq.venueId)
        _ <- validateZoneCapacity(showReq, venue)
        show <- showsRepository.addShow(showReq)

        performances <- DBIO.sequence(
          showReq.performancesDates.map { date =>
            performancesRepository.addPerformance(show.id, date, true)
          }
        )
        zones <- DBIO.sequence(
          showReq.zones.map { zone =>
            zonesRepository.addZone(zone,show.id)
          }
        )

        _ <- DBIO.sequence{
          for {
            performance <- performances
            zone <- zones
          } yield remainingsTicketsRepository.addTicket(performance, zone)
        }

      } yield show

      Try(Await.result(db.run(x.transactionally), 10.seconds)).toEither
    }
  }

  def getAllShows: Either[Throwable, Seq[ShowSoldOut]] = {
    val getShowsQuery = for {
      shows <- showsRepository.getAllShows
      showsSoldOut <- DBIO.sequence(
        shows.map { show =>
          for {
            showPerformances <- performancesRepository.getPerfromanceShowById(show.id)
            showZones <- zonesRepository.getZoneByShowId(show.id)
            showRemainings <- DBIO.sequence(
              for {
                showPerformance <- showPerformances
                showZone <- showZones
              } yield remainingsTicketsRepository.getTicketByPerformanceAndShowId(showPerformance.id, showZone.showId)
            )
          } yield ShowSoldOut(show.id, show.name, show.venueId, show.category, showPerformances, showZones, showRemainings)
        }
      )
    } yield showsSoldOut

    Try(Await.result(db.run(getShowsQuery), 10.seconds)).toEither
  }


  def getShowById(showId: Long): Either[Throwable, ShowResponseRemainings] = {
      val x = for {
        show <- showsRepository.getShowById(showId)
        performances <- performancesRepository.getPerfromanceShowById(showId)
        zones <- zonesRepository.getZoneByShowId(showId)
        remainings <- DBIO.sequence(for {
          performance <- performances
          zone <- zones
        } yield remainingTicketsRepository.getTicketById(showId, zone.id, performance.id))

      } yield ShowResponseRemainings(show, performances, zones, remainings,remainings.forall(_.remaining == 0))

      Try(Await.result(db.run(x), 10.seconds)).toEither
    }

  def pauseShow(showId: Long, pauseReq: PauseRequest): Either[Throwable, Performance] = {
    val pausePerformance =
    for {
      _ <- showsRepository.getShowById(showId)
      _ <- performancesRepository.getPerformanceById(pauseReq.performanceId)
      _ <- performancesRepository.pausePerformance(pauseReq)
      performance <- performancesRepository.getPerformanceById(pauseReq.performanceId)
    } yield performance

    Try(Await.result(db.run(pausePerformance),10.seconds)).toEither
  }

}