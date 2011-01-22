package com.appspot.mondayflicks

import util._

import org.scalatra._

/**
 * I contain test functions for authentication.
 * The application runs also without this filter.
 */
class AuthenticationScalatraFilter extends ScalatraFilter 
with CalendarAccessSupport with TweeterSupport with UserSupport with Logging {

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

  get("/admin/twitter") {
    tweeter.homeTimeline
  }

  get("/admin/tweet/:status") {
    tweeter.tweet(params('status))
  }

}

