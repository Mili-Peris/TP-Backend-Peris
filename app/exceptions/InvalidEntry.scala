package exceptions

class InvalidEntry (msg: String) extends Throwable(msg)

case class ShowNotFoundException(msj: String) extends InvalidEntry(msj)

case class VenueNotFoundException(msj: String) extends InvalidEntry(msj)

case class ZoneNotFoundException(msj: String) extends InvalidEntry(msj)

case class BookingNotFoundException(msj: String) extends InvalidEntry(msj)

case class PerformanceNotFoundException(msj: String) extends InvalidEntry(msj)

case class UserNotFoundException(msj: String) extends InvalidEntry(msj)

case class TicketNotFoundException(msj: String) extends InvalidEntry(msj)

case class DatabaseServerError(msj: String) extends InvalidEntry(msj)


