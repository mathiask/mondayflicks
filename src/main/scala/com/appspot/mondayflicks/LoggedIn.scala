package com.appspot.mondayflicks

import javax.servlet.http.HttpServletRequest

trait LoggedIn {
  protected def isLoggedIn(request: HttpServletRequest): Boolean = 
    request.getUserPrincipal != null
}
