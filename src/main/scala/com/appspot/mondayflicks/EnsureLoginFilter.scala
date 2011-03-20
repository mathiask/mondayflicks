package com.appspot.mondayflicks

import javax.servlet._
import http.{HttpServletResponse, HttpServletRequest}

class EnsureLoginFilter extends Filter with LoggedIn {

  def init(ignore: FilterConfig) {}
  def destroy {}

  def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    if (!isLoggedIn(request.asInstanceOf[HttpServletRequest])) {
      response.asInstanceOf[HttpServletResponse].sendRedirect("/login")
    } else {
      chain.doFilter(request, response)
    }
  }

}
