package com.appspot.mondayflicks

import util._
import org.scalatra._

import javax.servlet.FilterConfig

/**
 * I contain helper functions for authentication like the OAuth "dance".
 */
class AuthenticationScalatraFilter extends ScalatraFilter 
with CalendarAccessSupport with UserSupport with Logging {

  override def initialize(config: FilterConfig): Unit = {
    super.initialize(config)
    initializeCalendar(config)
  }

  get("/user/principal") {
    val user = currentUser
    val principal =
      request.getUserPrincipal + ", admin: " + isAdmin + " / " + user.getNickname + "(" + user.getEmail + ")"
    debug("Principal: " + principal)
    principal
  }

  get("/admin/cal/private") {
    calendar.readCalendar
  }

  get("/admin/cal/private/create/:title") {
    calendar.create(params('title))
  }
}

