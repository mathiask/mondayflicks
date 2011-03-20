package com.appspot.mondayflicks

import javax.servlet.http.HttpServletRequest

trait LoggedIn {
  protected def isLoggedIn(request: HttpServletRequest): Boolean = 
    isLoggedInWithGoogle(request) || isLoggedInWithCustom(request)

  protected def isLoggedInWithGoogle(request: HttpServletRequest) = request.getUserPrincipal != null
  protected def isLoggedInWithCustom(request: HttpServletRequest) = request.getSession.getAttribute("user") != null
}
