package com.proofpoint.domain.dao

import SidActivityDAO.SidActivity
import slick.driver.PostgresDriver.MappedColumnType
import slick.driver.PostgresDriver.api._
import org.joda.time.DateTime
import org.joda.time.DateTimeZone.UTC

class SidActivityTable(tag: Tag) extends Table[SidActivity](tag, "sid_activity") {

  implicit val JavaUtilDateMapper =
    MappedColumnType .base[org.joda.time.DateTime, java.sql.Timestamp] (
      d => new java.sql.Timestamp(d.getMillis),
      d => new DateTime(d getTime, UTC))

  def sid = column[Long]("sid", O.PrimaryKey)
  def count = column[Long]("count")
  def last_seen_date = column[DateTime]("last_seen_date", O.PrimaryKey)
  def create_date = column[DateTime]("create_date", O.PrimaryKey)
  def update_date = column[DateTime]("update_date")
  def * = (sid, count, last_seen_date, create_date, update_date) <> (SidActivity.tupled, SidActivity.unapply)

}