package com.appspot.mondayflicks

import org.scalatra.ScalatraKernel
import com.google.appengine.api.users._

import javax.servlet.http.HttpServletRequest

trait UserSupport extends LoggedIn {
  this: ScalatraKernel => 

  private lazy val userService = UserServiceFactory.getUserService

  protected def currentUser = session.getOrElse('user, userService.getCurrentUser).asInstanceOf[User]
  protected def isLoggedIn: Boolean = isLoggedIn(request)
  protected def isCustomLoggedIn = isLoggedInWithCustom(request)

  protected def isAdmin = request.isUserInRole("admin")
  protected def loginURL: String = loginURL(thisURL)
  protected def loginURL(nextURL: String): String = userService createLoginURL nextURL
  protected def cgLoginURL = "/login?next=" + thisURL
  protected def logoutURL = if (isLoggedInWithGoogle(request)) userService createLogoutURL thisURL
                            else "/login/logout"

  protected def cgChangePasswordURL = "/login/user/change?next=" + thisURL
  

  private def thisURL = request.getRequestURI
}
