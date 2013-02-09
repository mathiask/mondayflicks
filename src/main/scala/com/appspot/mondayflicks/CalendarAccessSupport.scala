package com.appspot.mondayflicks

import javax.servlet.FilterConfig

import scala.util.parsing.json.JSON

import org.scalatra._

trait CalendarAccessSupport extends Initializable
with util.UrlHelper with util.HttpHelper
with util.Logging {

  override type Config = FilterConfig

  protected var calendar: CalendarAccess = _
  private var clientId: String = _
  private var clientSecret: String = _

  override abstract def initialize(config: FilterConfig) {
    super.initialize(config)
    calendar = new DummyCalendarAccess
  }

  protected def setClientIdAndSecret(id: String, secret: String) {
    clientId = id
    clientSecret = secret
  }

  protected def oauth2url(returnPath: String) =
    "https://accounts.google.com/o/oauth2/auth?" +
    "response_type=code&client_id=" + clientId +
    "&redirect_uri=" + fullUrlEncoded(returnPath) +
    "&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fcalendar"

  protected def exchangeCodeForToken(code: String, returnPath: String) {
    val json = postRequest("https://accounts.google.com/o/oauth2/token",
                           None,
                           "code=" + urlEncode(code) +
                           "&client_id=" + urlEncode(clientId) +
                           "&client_secret=" + urlEncode(clientSecret) +
                           "&redirect_uri=" + fullUrlEncoded(returnPath) +
                           "&grant_type=authorization_code",
                           "application/x-www-form-urlencoded")
    calendar =
      new GoogleCalendarAccess(JSON.parseFull(json).get.asInstanceOf[Map[String, String]]("access_token"))
  }


  // override abstract def initialize(config: FilterConfig): Unit = {
  //   super.initialize(config)
  //   val context = config.getServletContext
  //   val calendarToken = context getInitParameter "calendar-token"
  //   calendar = if (calendarToken != null)
  //     new GoogleCalendarAccess(calendarToken, context getInitParameter "calendar-token-secret")
  //     else {
  //       warn("No calendar-token: configuring dummy implementation")
  //       new DummyCalendarAccess
  //     }
  // }

}
