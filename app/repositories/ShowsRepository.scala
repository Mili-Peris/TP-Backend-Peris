package repositories

import controllers.requests.ShowsRequest
import databases.entities.Show
import databases.tables.{PerformancesTable, ShowsTable}
import exceptions.ShowNotFoundException
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global


class ShowsRepository {

  private val showsTable = TableQuery[ShowsTable]
  private val performancesTable = TableQuery[PerformancesTable]

  def getAllShows = showsTable.result

  def getShowById(showId: Long) = {
    showsTable.filter(_.id === showId).result.headOption.flatMap {
      case Some(value) => DBIO.successful(value)
      case None => DBIO.failed(ShowNotFoundException("show not found"))
    }
  }

  def addShow(showsRequest: ShowsRequest) = {
    val newShow = Show(Int.MinValue, showsRequest.name, showsRequest.category, showsRequest.venueId)
    showsTable returning showsTable += newShow
  }


}