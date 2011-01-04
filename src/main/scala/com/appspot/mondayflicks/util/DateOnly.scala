package com.appspot.mondayflicks.util

import java.text.SimpleDateFormat
import java.util.{Date, Calendar, GregorianCalendar, TimeZone}
import Calendar._

import com.google.api.client.util.DateTime

/** I am a Java Data considered up to day, ignoring time. */
class DateOnly(date: Date) {
  import DateOnly._

  override def toString = dateFormat.format(date)

  def toDate = {
    val cal = dateAsCalendar
    cal.set(HOUR, 0)
    cal.set(MINUTE, 0)
    cal.set(SECOND, 0)
    cal.set(MILLISECOND, 0)
    cal.getTime
  } 

  private def dateAsCalendar = {
    val cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"))
    cal.setTime(date)
    cal
  }

  def isBefore(another: DateOnly) = {
    val myCal = dateAsCalendar
    val theirCal = another.dateAsCalendar
    myCal.get(YEAR) < theirCal.get(YEAR) || 
      myCal.get(YEAR) == theirCal.get(YEAR) && myCal.get(DAY_OF_YEAR) < theirCal.get(DAY_OF_YEAR)
  }

  def isBeforeToday = isBefore(today)
}

object DateOnly {
  private def dateFormat = new SimpleDateFormat("yyyy-MM-dd")

  /** For testing. */
  private[util] var dateFactory = () => new Date

  implicit def apply(date: Date): DateOnly = new DateOnly(date)
  implicit def apply(dateTime: DateTime): DateOnly = new DateOnly(new Date(dateTime.value))
  def apply(iso8601String: String): DateOnly = apply(dateFormat parse iso8601String)
  def today = apply(dateFactory())

  implicit def dateOnlyToDate(d: DateOnly) = d.toDate
  implicit def dateOnlyToDateTime(d: DateOnly) = new DateTime(true, d.toDate.getTime, 0)
}
