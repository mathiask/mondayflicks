package com.appspot.mondayflicks

import javax.servlet.FilterConfig
import org.scalatra._

trait CalendarAccessSupport extends Initializable with util.Logging {

  override type Config = FilterConfig

  protected var calendar: CalendarAccess = _

  override abstract def initialize(config: FilterConfig): Unit = {
    super.initialize(config)
    val context = config.getServletContext
    val calendarToken = context getInitParameter "calendar-token"
    calendar = if (calendarToken != null) 
      new GoogleCalendarAccess(calendarToken, context getInitParameter "calendar-token-secret")
      else {
        warn("No calendar-token: configuring dummy implementation")
        new DummyCalendarAccess
      }
  }

}
