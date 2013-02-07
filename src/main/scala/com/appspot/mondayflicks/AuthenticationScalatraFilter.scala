package com.appspot.mondayflicks

import scala.collection.JavaConversions._

import org.scalatra._

/**
 * I contain test functions for authentication.
 * The application runs also without this filter.
 */
class AuthenticationScalatraFilter extends ScalatraFilter
with CalendarAccessSupport with TweeterSupport with UserSupport
with util.UrlHelper
with util.Logging  {

  get("/user/principal") {
    val user = currentUser
    val principal =
      request.getUserPrincipal + ", admin: " + isAdmin + " / " + user.getNickname + "(" + user.getEmail + ")"
    debug("Principal: " + principal)
    principal
  }

  get("/admin/twitter") {
    tweeter.homeTimeline
  }

  get("/admin/tweet/:status") {
    tweeter.tweet(params('status))
  }

  // doesn't work for Google Calendar
  // get("/admin/appidentitytoken") {
  //   var token = AppIdentityServiceFactory.getAppIdentityService.getAccessToken(List("https://www.googleapis.com/auth/calendar"))
  //   session('AccessToken) = token.getAccessToken
  //   token.getAccessToken + " : " + token.getExpirationTime
  // }

  get("/admin/oauth2") {
    <form action="/admin/oauth2" method="POST">
      <h1>Get Access Token</h1>
      <label for="cid">Client ID:</label> <input id="cid" type="text" name="cid"/>
      <label for="csec">Client Secret:</label> <input id="csec" type="text" name="csec"/>
      <input type="submit" value="Get token"/>
    </form>
  }

  post("/admin/oauth2") {
    setClientIdAndSecret(params('cid), params('csec))
    redirect(oauth2url(tokenPath))
  }

  private val tokenPath = "/admin/token"

  get(tokenPath) {
    exchangeCodeForToken(params('code), tokenPath)
    "Token received!"
  }

  get("/admin/cal/private") {
    if(!calendar.hasToken)
      "No access token!"
    else {
      getRequest("https://www.googleapis.com/calendar/v3/calendars/pvbp2e5h4t4mhigof30lkq5abc%40group.calendar.google.com",
                 calendar.token.get)
    }
  }

  // get("/admin/cal/private/create/:title") {
  //   calendar.create(params('title))
  // }

}
