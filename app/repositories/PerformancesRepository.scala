package repositories

import controllers.requests.PauseRequest
import databases.entities.Performance
import databases.tables.PerformancesTable
import exceptions.PerformanceNotFoundException
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import java.time.LocalDate

class PerformancesRepository {

  private val performancesTable = TableQuery[PerformancesTable]

  def getPerfromanceShowById(showId: Long) = {
    performancesTable.filter(p =>p.showId === showId).result
      /*.headOption.flatMap {
      case Some(x) => DBIO.successful(x)
      case None => DBIO.failed(PerformanceNotFoundException("wrong performance id"))
      }
       */
  }


  def getPerformanceById(performanceId: Long) = {
    performancesTable.filter(_.id === performanceId).result.headOption.flatMap {
      case Some(x) => DBIO.successful(x)
      case None => DBIO.failed(PerformanceNotFoundException("performance not found"))
    }
  }
  def addPerformance(showId: Long, date: LocalDate, active: true) = {
    val newPerformance = Performance(Int.MinValue, date, active, showId)
    performancesTable returning performancesTable += newPerformance
  }

  def pausePerformance(pauseReq: PauseRequest) = {
    val updateActive = performancesTable.filter(_.id === pauseReq.performanceId).map(_.active).update(pauseReq.performanceState)
    updateActive
  }

}