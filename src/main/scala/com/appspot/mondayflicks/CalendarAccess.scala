package com.appspot.mondayflicks

import util.Logging
import scala.collection.JavaConversions._
import scala.util.parsing.json._

import com.google.api.client.util.{Key, DateTime}

trait CalendarAccess {
  def create(film: Film): String
  def delete(id: String): Unit
  def update(film: Film): Unit
}

class DummyCalendarAccess extends CalendarAccess with Logging {
  def create(film: Film) = { info("Dummy create calendar for " + film); "dummy" }
  def delete(id: String) = info("Dummy calendar deleting " + id)
  def update(film: Film) = info("Dummy syncinging " + film)
}

class GoogleCalendarAccess(token: String) extends CalendarAccess
with util.HttpHelper with Logging {
  val baseUrl = "https://www.googleapis.com/calendar/v3/calendars/pvbp2e5h4t4mhigof30lkq5abc@group.calendar.google.com/events"

  def create(film: Film) = try {
    debug("Creating film" + film.title + "...")
    val j = JSONObject(Map("summary" -> film.title,
                           "start" -> JSONObject(Map("date" -> film.scheduled.toString)),
                           "end" -> JSONObject(Map("date" -> film.scheduled.toString))))
    JSON.parseFull(postRequest(baseUrl, Some(token), j)).get.asInstanceOf[Map[String, String]]("id")
  } catch { // don't fail because of calendar
    case e => severe(e); null
  }

  def delete(id: String) = try {
    debug("Deleting film " + id + "...")
    deleteRequest(baseUrl + "/" + id, Some(token))
  } catch { // don't fail because of calendar
    case e => severe(e); null
  }

  def update(film: Film) = try {
    debug("Updating...")
    val j = JSONObject(Map("summary" -> film.title,
                           "start" -> JSONObject(Map("date" -> film.scheduled.toString)),
                           "end" -> JSONObject(Map("date" -> film.scheduled.toString))))
    putRequest(baseUrl + "/" + film.calendarId, Some(token), j)
  } catch { // don't fail because of calendar
    case e => severe(e); null
  }
}
