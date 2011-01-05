package com.appspot.mondayflicks

import javax.servlet.FilterConfig

trait CalendarAccessSupport {

  protected var calendar: CalendarAccess = _

  def initializeCalendar(config: FilterConfig): Unit = {
    calendar = new CalendarAccess(config.getServletContext getInitParameter "calendar-token",
                                  config.getServletContext getInitParameter "calendar-secret")
  }

}
