package com.appspot.mondayflicks

import org.scalatra.ScalatraKernel

import javax.servlet.http.HttpServletRequest

trait UserSupport extends LoggedIn {
  this: ScalatraKernel => 

  private lazy val userService = com.google.appengine.api.users.UserServiceFactory.getUserService

  protected def currentUser = userService.getCurrentUser
  protected def isLoggedIn: Boolean = isLoggedIn(request)
  protected def isAdmin = request.isUserInRole("admin")
  protected def logoutURL = userService createLogoutURL thisURL
  protected def loginURL: String = loginURL(thisURL)
  protected def loginURL(nextURL: String): String = userService createLoginURL nextURL

  private def thisURL = request.getRequestURI
}
