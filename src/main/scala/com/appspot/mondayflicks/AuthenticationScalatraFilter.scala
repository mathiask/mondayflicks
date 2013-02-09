package com.appspot.mondayflicks

import scala.collection.JavaConversions._
import org.scalatra._
import util.DateOnly


/**
 * I contain test functions for authentication.
 * The application runs also without this filter.
 */
class AuthenticationScalatraFilter extends ScalatraFilter
with CalendarAccessSupport with TweeterSupport with UserSupport
with util.UrlHelper with util.HttpHelper
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

  // Doesn't work for Google Calendar
  //
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

  get("/admin/oauth2!") {
    redirect(oauth2url(tokenPath))
  }

  private val tokenPath = "/admin/token"

  get(tokenPath) {
    exchangeCodeForToken(params('code), tokenPath)
    "Token received!"
  }

  get("/admin/cal/private/create/:title") {
    var film = new Film
    film.title = params('title)
    film.scheduled = DateOnly.today
    calendar.create(film)
  }

  get("/admin/cal/private/delete/:id") {
    calendar.delete(params('id))
    "Deleted."
  }

  get("/admin/cal/private/update/:id/:title/:date") {
    var film = new Film
    film.calendarId = params('id)
    film.title = params('title)
    film.scheduled = DateOnly(params('date))
    calendar.update(film)
    "Updated."
  }

}
