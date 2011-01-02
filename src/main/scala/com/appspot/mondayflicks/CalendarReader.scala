package com.appspot.mondayflicks

import com.google.api.client.util.Key
import com.google.api.client.googleapis.GoogleUrl

class CalendarReader(token: String, secret: String) extends OAuthResourceReader(token, secret) {
  val url = new GoogleUrl("http://www.google.com/calendar/feeds/pvbp2e5h4t4mhigof30lkq5abc%40group.calendar.google.com/private/full")
  url.alt = "jsonc"
  url.prettyprint = true

  def readCalendar: String = {
    val feed =  getFollowingRedirect(url).parseAs(classOf[Feed])
    feed.title + "\n" + feed.items
  }
}

class Feed {
  import com.google.api.client.util.Key
  @Key var title: String = _
  @Key var items: java.util.List[Item] = _
}

class Item {
  import com.google.api.client.util.Key
  @Key var title: String = _
  override def toString = "Title: " + title
}
