package com.appspot.mondayflicks

import util.Logging
import scala.collection.JavaConversions._

import com.google.api.client.util.{Key, DateTime}
import com.google.api.client.googleapis.json.JsonCContent
import com.google.api.client.googleapis.GoogleUrl

trait CalendarAccess {
  def readCalendar: String
  def create(film: Film): String
  def create(title: String): String
  def delete(id: String): Unit
  def reschedule(film: Film): Unit
  def rename(film: Film): Unit
}

class DummyCalendarAccess extends CalendarAccess with Logging {
  def readCalendar = "dummy calendar"
  def create(film: Film) = { info("Dummy create calendar for " + film); "dummy" }
  def create(title: String) = { info("Dummy create calendar for " + title); "dummy" }
  def delete(id: String) = info("Dummy calendar deleting " + id)
  def reschedule(film: Film) = info("Dummy rescheduling " + film)
  def rename(film: Film) = info("Dummy renaming " + film)
}


class GoogleCalendarAccess(token: String, secret: String) extends OAuthRestResource(token, secret) 
with Logging with CalendarAccess {

  private val baseUrlString = 
    "http://www.google.com/calendar/feeds/pvbp2e5h4t4mhigof30lkq5abc%40group.calendar.google.com/private/full"
  private val url = new GoogleUrl(baseUrlString)
  url.alt = "jsonc"
  url.prettyprint = true

  def readCalendar: String = {
    val feed = getFollowingRedirect(url).parseAs(classOf[Feed])
    feed.title + "\n" + feed.items
  }

  def create(film: Film) = try {
    debug("Creating film...")
    val content = new JsonCContent
    content.data = Event(film.title, film.scheduled)
    val event = postFollowingRedirect(url, content).parseAs(classOf[Event])
    debug("... with id " + event.id)
    event.id
  } catch { // don't fail because of calendar
    case e => error(e); null
  }

  def create(title: String) = {
    val content = new JsonCContent
    content.data = Event(title, util.DateOnly.today)
    val event = postFollowingRedirect(url, content).parseAs(classOf[Event])
    event.id
  }

  def delete(id: String) {
    debug("Deleting film " + id + "...")
    if (id == null) return
    try deleteFollowingRedirect(urlFor(id))
    catch { // don't fail because of calendar
      case e => error(e)
    }
  }

  private def urlFor(id: String) = {
    new GoogleUrl(baseUrlString + "/" + id)
  }

  def reschedule(film: Film) = syncNameAndDate(film)

  private def syncNameAndDate(film: Film) = try {
    assert(film.calendarId != null)
    val event = readEvent(film.calendarId)
    event.title = film.title
    event.setStartAndEnd(film.scheduled)
    put(event)
  } catch { // don't fail because of calendar
    case e => error(e)
  }

  private def readEvent(id :String) =
    getFollowingRedirect(jasonUrlFor(id)).parseAs(classOf[Event])

  private def jasonUrlFor(id: String) = {
    val result = urlFor(id)
    result.alt = "jsonc"
    result
  }

  private def put(event: Event): Unit = try {
    val content = new JsonCContent
    content.data = event
    putFollowingRedirect(jasonUrlFor(event.id), content)
  } catch { // don't fail because of calendar
    case e => error(e)
  }

  def rename(film: Film) = syncNameAndDate(film)

}

class Feed {
  @Key var title: String = _
  @Key var items: java.util.List[Event] = _
}

class Event {
  @Key var id: String = _
  @Key var title: String = _
  @Key var when: java.util.List[EventSchedule] = _
  override def toString = "Title: " + title + "(" + when + ") [#" + id + "]"

  def setStartAndEnd(day: DateTime) {
    assert(when.size == 1)
    val es = when.head
    es.start = day
    es.end = day
  }
}
object Event {
  def apply(title: String, day: DateTime) = {
    import scala.collection.JavaConversions._
    val item = new Event
    item.title = title
    val when = new EventSchedule
    when.start = day
    when.end = day
    item.when = List(when)
    item
  }
}

class EventSchedule {
  @Key var start: DateTime = _
  @Key var end: DateTime = _
  override def toString = start + " - " + end
}
