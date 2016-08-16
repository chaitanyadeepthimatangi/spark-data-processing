package com.proofpoint.domain.dao

import java.sql.Date

import com.proofpoint.domain.dao.SidActivityDAO.SidActivity
import com.proofpoint.domain.utils.DbProvider
import org.slf4j.LoggerFactory
import org.joda.time.DateTime
import slick.driver.PostgresDriver.MappedColumnType
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.{Success, Failure}

object SidActivityDAO {
  case class SidActivity(sid: Long,
                         count: Long,
                         last_seen_date: DateTime,
                         create_date: DateTime,
                         update_date: DateTime)
}

trait SidActivityDAO {
  def insertSidActivity(sidActivity: Seq[SidActivity])
}

class SidActivityDaoImpl extends SidActivityDAO with DbProvider {
  def logger = LoggerFactory.getLogger(SidActivityDaoImpl.this.getClass)


  implicit def dateTimeMapper = MappedColumnType.base[DateTime, Date] (
    { dateTime => new Date(dateTime.getMillis) },
    { date => new DateTime(date) }
  )

  lazy val sidActivity = TableQuery[SidActivityTable]

  def insertSidActivity(sidActivityInput: Seq[SidActivity]) = {
    val groupedSidActivity = sidActivityInput
      .groupBy(sidResults => (sidResults.sid, sidResults.last_seen_date))
      .mapValues(_.map(_.count).sum)
    groupedSidActivity.foreach( sidInput => {
      val groupedCount = sidInput._2
      val groupedSid = sidInput._1._1
      val groupedLastSeenDate = sidInput._1._2
      val q = fetchSidActivityByDate(groupedSid, groupedLastSeenDate)
      val upsert = for {
        existing <- db.run(q.result.headOption)
        existingCount = existing.map(_.count).getOrElse(1L)
        row = existing.map(_.copy(count= (groupedCount + existingCount), update_date= new DateTime()))
          .getOrElse(SidActivity(groupedSid,
            groupedCount,
            groupedLastSeenDate,
            new DateTime(),
            new DateTime()
          ))
      } yield row

      upsert.onComplete({
        case Success(v) => {
          try {
            //db.run(sidActivity.insertOrUpdate(v))
            db.run(sqlu"""INSERT INTO sid_activity AS sid_act (sid, count, last_seen_date, create_date, update_date) +
                   VALUES (${v.sid}, ${v.count}, ${v.last_seen_date}, ${v.create_date}, ${v.update_date}) +
                   ON CONFLICT (sid, last_seen_date, create_date) DO UPDATE +
                   SET count = ${v.count} and update_date = ${v.update_date}
                   WHERE sid = ${v.sid}
                 """)
          }catch {
            case e: Exception => logger.error("Exception handling insert or update" , e)
          }
        }
        case Failure(v) => logger.error("Exception handling futures for upsert")
      })
    })
  }

  def fetchSidActivityByDate(sid: Long, date: DateTime) =
    sidActivity.filter(x => (x.sid === sid) && (x.last_seen_date === new DateTime(date)))
}
