package com.appspot.mondayflicks.util

import java.io.OutputStreamWriter
import java.net.{URL, HttpURLConnection}

import scala.util.parsing.json.JSONObject

trait HttpHelper extends Logging {
  protected def getRequest(url: String, token: Option[String]): String =
    simpleRequest(url, "GET", token)

  private def simpleRequest(url: String, method: String, token: Option[String]): String =
    request(url, method, token){ _ => () }

  private def request(url: String,
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

  protected def deleteRequest(url: String, token: Option[String]): String =
    simpleRequest(url, "DELETE", token)

  protected def postRequest(url: String,
                            token: Option[String],
                            body: String,
                            contentType:String = "application/json"): String =
    bodyRequest(url, "POST", token, body, contentType)

  private def bodyRequest(url: String,
                          method: String,
                          token: Option[String],
                          body: String,
                          contentType:String): String =
    request(url, method, token){
      con =>
        debug("Sending body:\n" + body)
        con.addRequestProperty("Content-Type", contentType)
        val writer = new OutputStreamWriter(con getOutputStream)
        writer.write(body)
        writer.close
    }

  protected def postRequest(url: String,
                            token: Option[String],
                            json: JSONObject): String =
    postRequest(url, token, json.toString())

  protected def putRequest(url: String,
                           token: Option[String],
                           body: String,
                           contentType:String = "application/json"): String =
    bodyRequest(url, "PUT", token, body, contentType)

  protected def putRequest(url: String,
                           token: Option[String],
                           json: JSONObject): String =
    putRequest(url, token, json.toString())
}
