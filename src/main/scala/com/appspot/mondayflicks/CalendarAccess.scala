package com.appspot.mondayflicks

import com.google.api.client.util.{Key, DateTime}
import com.google.api.client.googleapis.json.JsonCContent
import com.google.api.client.googleapis.GoogleUrl

class CalendarAccess(token: String, secret: String) extends OAuthRestResource(token, secret) with util.Logging {
  val url = new GoogleUrl("http://www.google.com/calendar/feeds/pvbp2e5h4t4mhigof30lkq5abc%40group.calendar.google.com/private/full")
  url.alt = "jsonc"
  url.prettyprint = true

  def readCalendar: String = {
    val feed =  getFollowingRedirect(url).parseAs(classOf[Feed])
    feed.title + "\n" + feed.items
  }

  def create(title: String) = {
    import util.DateOnly.today
    val content = new JsonCContent
    content.data = Event(title, today, today)
    val event = postFollowingRedirect(url, content).parseAs(classOf[Event])
    event.id
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
  def apply(title: String, start: DateTime, end: DateTime) = {
    import scala.collection.JavaConversions._
    val item = new Event
    item.title = title
    val when = new EventSchedule
    when.start = start
    when.end = end
    item.when = List(when)
    item
  }
}

class EventSchedule {
  @Key var start: DateTime = _
  @Key var end: DateTime = _
  override def toString = start + " - " + end
}

