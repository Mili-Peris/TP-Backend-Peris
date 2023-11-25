package databases.entities
import java.time.LocalDate

case class Performance(id: Long, date: LocalDate, active: Boolean, showId: Long)

