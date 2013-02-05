package com.appspot.mondayflicks

import util._

import scala.collection.JavaConversions._
import scala.util.parsing.json.JSON

import org.scalatra._

import com.google.appengine.api.appidentity.AppIdentityServiceFactory

import java.net.{URL, HttpURLConnection, URLEncoder}

/**
 * I contain test functions for authentication.
 * The application runs also without this filter.
 */
class AuthenticationScalatraFilter extends ScalatraFilter
with CalendarAccessSupport with TweeterSupport with UserSupport
with UrlHelper
with Logging  {

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

  get("/admin/appid") {
    AppIdentityServiceFactory.getAppIdentityService.getServiceAccountName
  }

  get("/admin/appidentitytoken") {
    var token = AppIdentityServiceFactory.getAppIdentityService.getAccessToken(List("https://www.googleapis.com/auth/calendar"))
    session('AccessToken) = token.getAccessToken
    token.getAccessToken + " : " + token.getExpirationTime
  }

  get("/admin/oauth2") {
    <form action="/admin/oauth2" method="POST">
      <h1>Get Access Token</h1>
      <label for="cid">Client ID:</label> <input id="cid" type="text" name="cid"/>
      <label for="csec">Client Secret:</label> <input id="csec" type="text" name="csec"/>
      <input type="submit" value="Get token"/>
    </form>
  }

  post("/admin/oauth2") {
    session('ClientId) = params('cid)
    session('ClientSecret) = params('csec)
    redirect("https://accounts.google.com/o/oauth2/auth?" +
             "response_type=code&client_id=" + params('cid) +
             "&" + tokenRedirectParam +
             "&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fcalendar")
  }

  private def tokenRedirectParam =
    "redirect_uri=" + fullUrlEncoded("/admin/token")

  get("/admin/token") {
    val json = postRequest("https://accounts.google.com/o/oauth2/token",
                           None,
                           "code=" + urlEncode(params('code)) +
                             "&client_id=" + urlEncode(session('ClientId).asInstanceOf[String]) +
                             "&client_secret=" + urlEncode(session('ClientSecret).asInstanceOf[String]) +
                             "&" + tokenRedirectParam +
                             "&grant_type=authorization_code",
                           "application/x-www-form-urlencoded")
    session('AccessToken) = JSON.parseFull(json).get.asInstanceOf[Map[String, String]]("access_token")
    "Token received!"
  }

  get("/admin/cal/private") {
    if(!haveAccesToken)
      "No access token!"
    else {
      getRequest("https://www.googleapis.com/calendar/v3/calendars/pvbp2e5h4t4mhigof30lkq5abc%40group.calendar.google.com", accesToken)
    }
  }

  private def haveAccesToken = session.get('AccessToken).isDefined
  private def accesToken = session('AccessToken).asInstanceOf[String]

  // get("/admin/cal/private/create/:title") {
  //   calendar.create(params('title))
  // }

}
