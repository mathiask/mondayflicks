package com.appspot.mondayflicks

import javax.servlet.FilterConfig
import org.scalatra._

trait CalendarAccessSupport extends Initializable {

  override type Config = FilterConfig

  protected var calendar: CalendarAccess = _

  override abstract def initialize(config: FilterConfig): Unit = {
    super.initialize(config)
    val context = config.getServletContext
    calendar = new CalendarAccess(context getInitParameter "calendar-token",
                                  context getInitParameter "calendar-token-secret")
  }

}
