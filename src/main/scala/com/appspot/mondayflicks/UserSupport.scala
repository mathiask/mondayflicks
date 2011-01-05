package com.appspot.mondayflicks

import javax.servlet.http.HttpServletRequest

trait UserSupport {
  private lazy val userService = com.google.appengine.api.users.UserServiceFactory.getUserService

  protected def currentUser = userService.getCurrentUser
  protected def isLoggedIn = request.getUserPrincipal != null
  protected def isAdmin = request.isUserInRole("admin")
  protected def logoutURL = userService createLogoutURL thisURL
  protected def loginURL = userService createLoginURL thisURL

  private def thisURL = request.getRequestURI
  protected def request: HttpServletRequest
}
