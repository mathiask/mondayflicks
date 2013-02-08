package com.appspot.mondayflicks

import util.Logging
import scala.collection.JavaConversions._
import scala.util.parsing.json._

import com.google.api.client.util.{Key, DateTime}

trait CalendarAccess {
  def token: Option[String]
  def hasToken = token.isDefined
  def create(film: Film): String
  def delete(id: String): Unit
  def update(film: Film): Unit
}

class DummyCalendarAccess extends CalendarAccess with Logging {
  def token: Option[String] = None
  def create(film: Film) = { info("Dummy create calendar for " + film); "dummy" }
  def delete(id: String) = info("Dummy calendar deleting " + id)
  def update(film: Film) = info("Dummy syncinging " + film)
}

// FIXME extend CalendarAccess
class GoogleCalendarAccess(_token: String) extends DummyCalendarAccess with Logging {
  override def token = Some(_token)

  override def create(film: Film) = try {
    debug("Creating film...")
    val j = JSONObject(Map("summary" -> "test", "start" -> JSONObject(Map("date" -> new java.util.Date))))
    // val content = new JsonCContent
    // content.data = Event(film.title, film.scheduled)
    // val event = postFollowingRedirect(url, content).parseAs(classOf[Event])
    // debug("... with id " + event.id)
    // event.id
    "42"
  } catch { // don't fail because of calendar
    case e => severe(e); null
  }

}

// class GoogleCalendarAccess(token: String, secret: String) extends OAuthRestResource(token, secret)
// with Logging with CalendarAccess {

//   private val baseUrlString =
//     "http://www.google.com/calendar/feeds/pvbp2e5h4t4mhigof30lkq5abc%40group.calendar.google.com/private/full"
//   private val url = new GoogleUrl(baseUrlString)
//   url.alt = "jsonc"
//   url.prettyprint = true

//   def create(film: Film) = try {
//     debug("Creating film...")
//     val content = new JsonCContent
//     content.data = Event(film.title, film.scheduled)
//     val event = postFollowingRedirect(url, content).parseAs(classOf[Event])
//     debug("... with id " + event.id)
//     event.id
//   } catch { // don't fail because of calendar
//     case e => severe(e); null
//   }

//   def create(title: String) = {
//     val content = new JsonCContent
//     content.data = Event(title, util.DateOnly.today)
//     val event = postFollowingRedirect(url, content).parseAs(classOf[Event])
//     event.id
//   }

//   def delete(id: String) {
//     debug("Deleting film " + id + "...")
//     if (id == null) return
//     try deleteFollowingRedirect(urlFor(id))
//     catch { // don't fail because of calendar
//       case e => severe(e)
//     }
//   }

//   private def urlFor(id: String) = {
//     new GoogleUrl(baseUrlString + "/" + id)
//   }

//   def reschedule(film: Film) = syncNameAndDate(film)

//   private def syncNameAndDate(film: Film) = try {
//     assert(film.calendarId != null)
//     val event = readEvent(film.calendarId)
//     event.title = film.title
//     event.setStartAndEnd(film.scheduled)
//     put(event)
//   } catch { // don't fail because of calendar
//     case e => severe(e)
//   }

//   private def readEvent(id :String) =
//     getFollowingRedirect(jasonUrlFor(id)).parseAs(classOf[Event])

//   private def jasonUrlFor(id: String) = {
//     val result = urlFor(id)
//     result.alt = "jsonc"
//     result
//   }

//   private def put(event: Event): Unit = try {
//     val content = new JsonCContent
//     content.data = event
//     putFollowingRedirect(jasonUrlFor(event.id), content)
//   } catch { // don't fail because of calendar
//     case e => severe(e)
//   }

//   def rename(film: Film) = syncNameAndDate(film)

// }

// class Feed {
//   @Key var title: String = _
//   @Key var items: java.util.List[Event] = _
// }

// class Event {
//   @Key var id: String = _
//   @Key var title: String = _
//   @Key var when: java.util.List[EventSchedule] = _
//   override def toString = "Title: " + title + "(" + when + ") [#" + id + "]"

//   def setStartAndEnd(day: DateTime) {
//     assert(when.size == 1)
//     val es = when.head
//     es.start = day
//     es.end = day
//   }
// }
// object Event {
//   def apply(title: String, day: DateTime) = {
//     import scala.collection.JavaConversions._
//     val item = new Event
//     item.title = title
//     val when = new EventSchedule
//     when.start = day
//     when.end = day
//     item.when = List(when)
//     item
//   }
// }

// class EventSchedule {
//   @Key var start: DateTime = _
//   @Key var end: DateTime = _
//   override def toString = start + " - " + end
// }
