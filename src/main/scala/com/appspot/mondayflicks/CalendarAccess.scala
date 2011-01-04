package com.appspot.mondayflicks

import com.google.api.client.util.{Key, DateTime}
import com.google.api.client.googleapis.json.JsonCContent
import com.google.api.client.googleapis.GoogleUrl

class CalendarAccess(token: String, secret: String) extends OAuthRestResource(token, secret) with util.Logging {

  private val baseUrlString = "http://www.google.com/calendar/feeds/pvbp2e5h4t4mhigof30lkq5abc%40group.calendar.google.com/private/full"
  private val url = new GoogleUrl(baseUrlString)
  url.alt = "jsonc"
  url.prettyprint = true

  def readCalendar: String = {
    val feed =  getFollowingRedirect(url).parseAs(classOf[Feed])
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
    case e => error(e); e.printStackTrace; null
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
    try deleteUrl(urlFor(id))
    catch { // don't fail because of calendar
      case e => warn(e)
    }
  }

  private def urlFor(id: String) = {
    new GoogleUrl(baseUrlString + "/" + id)
  }

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

