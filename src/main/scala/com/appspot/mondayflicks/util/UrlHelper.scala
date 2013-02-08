package com.appspot.mondayflicks.util

import java.io.OutputStreamWriter
import java.net.{URL, HttpURLConnection}
import java.net.URLEncoder.encode
import javax.servlet.http.HttpServletRequest

import org.scalatra.UrlSupport

trait UrlHelper extends UrlSupport with Logging {
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

  protected def getRequest(url: String, token: String): String =
    request(url, "GET", Some(token)){ _ => () }

  protected def request(url: String,
                  method: String,
                  token: Option[String])(f: HttpURLConnection => Unit): String = {
    val u = new URL(url)
    val connection = u.openConnection.asInstanceOf[HttpURLConnection]
    connection setDoOutput true
    connection setRequestMethod method
    for (t <- token)
      connection.addRequestProperty("Authorization", "Bearer " + t)
    debug("HTTP request:" + connection)
    f(connection)
    if (connection.getResponseCode == HttpURLConnection.HTTP_OK) {
      val s = new java.util.Scanner(connection getInputStream).useDelimiter("\\A")
      s next
    } else
      throw new RuntimeException("HTTP response " + connection.getResponseCode)
  }

  protected def postRequest(url: String,
                            token: Option[String],
                            body: String,
                            contentType:String = "application/json"): String =
    request(url, "POST", token){
      con =>
        debug("POSTing\n" + body)
        con.addRequestProperty("Content-Type", contentType)
        val writer = new OutputStreamWriter(con getOutputStream)
        writer.write(body)
        writer.close
    }

}
