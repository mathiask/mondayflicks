package com.appspot.mondayflicks

import util._
import scala.collection.JavaConversions._
import org.scalatra._
import com.google.appengine.api.appidentity.AppIdentityServiceFactory

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

  get("/admin/cal/test") {
    var token = AppIdentityServiceFactory.getAppIdentityService.getAccessToken(List("https://www.googleapis.com/auth/calendar"))
    token.getAccessToken + " : " + token.getExpirationTime
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
