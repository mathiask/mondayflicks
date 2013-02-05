package com.appspot.mondayflicks.util

import java.net.URLEncoder.encode
import javax.servlet.http.HttpServletRequest

import org.scalatra.UrlSupport

trait UrlHelper extends UrlSupport {
  override def contextPath: String = ""

  protected def request: HttpServletRequest

  protected def urlEncode(string: String) = encode(string, "UTF-8")

  protected def urlWithoutPath = {
    val Pattern = """(https?://[^/]*)/.*""".r
    request.getRequestURL match { case Pattern(u) => u }
  }

  protected def fullUrl(path: String) = urlWithoutPath + url(path)
  protected def fullUrlEncoded(path: String) =
    urlEncode(urlWithoutPath + url(path))
}
