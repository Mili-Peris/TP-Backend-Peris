package services

import com.google.inject.{Inject, Singleton}
import controllers.requests.VenuesRequest
import databases.entities.Venue
import exceptions._
import repositories.VenuesRepository
import slick.jdbc.JdbcBackend.Database
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util._

@Singleton
class VenuesService @Inject()(
                             venuesRepository: VenuesRepository
                             ) {

  private val db = Database.forConfig("postgres")
 //sacarlo y meterlo en inyeccion de dependencias

  def add(venueReq: VenuesRequest): Either[Throwable, Venue] = {
    for {
      _ <- validateReq(venueReq)
      res <- Try(Await.result(db.run(venuesRepository.addVenue(venueReq)), 10.seconds)).toEither
    } yield res
  }
  /*
  OTRA OPCION, MAS LARGA PERO VALIDA(QUIZA MAS ENTENDIBLE)
  def add(venueReq: VenuesRequest): Either[Throwable, Venue] = {
    val venueValidation = validationRequest(venueReq)
    venueValidation match {
      case Left(requestError) => Left(requestError) //error de request xq algo fallo en validacion, problema de usuario
      case Right(venue) =>
        val venueToAdd = venue
        val res = Try(Await.result(db.run(venuesRepository.addVenue(venueReq)), 10.seconds))

        res match {
          case Success(value) => Right(value)
          case Failure(exception) => Left(exception) //error de base de datos, problema nuestro
   */

  def getAllVenues: Either[Throwable, Seq[Venue]] = {
    for {
      result <- Try(Await.result(db.run(venuesRepository.getAllVenues), 10.seconds)).toEither
    } yield result
  }

  def getVenueById(venueId: Long) ={
    for {
      result <- Try(Await.result(db.run(venuesRepository.getVenueById(venueId)), 10.seconds)).toEither
    } yield result
  }

  def validateReq(venueReq: VenuesRequest): Either[Throwable, Unit] = {
    venueReq match {
      case k if k.capacity < 0 => Left(VenueNotFoundException("Capacity must be positive"))
      case k if k.name.isEmpty => Left(VenueNotFoundException("Name is requierd"))
      case _ => Right(())
    }
  }
}
