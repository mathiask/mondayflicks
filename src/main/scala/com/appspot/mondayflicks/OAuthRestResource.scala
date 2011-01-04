package com.appspot.mondayflicks

import com.google.api.client.googleapis.json._
import com.google.api.client.http._
import com.google.api.client.auth.oauth._

import util._

abstract class OAuthRestResource(token: String, secret: String) extends Logging {
  private val transport = new HttpTransport
  transport.defaultHeaders.put("GData-Version", "2")
  transport.addParser(new JsonCParser)

  val signer = new OAuthHmacSigner
  signer.clientSharedSecret = "anonymous"
  signer.tokenSharedSecret = secret

  private val parameters = new OAuthParameters
  parameters.consumerKey = "anonymous"
  parameters.token = token
  parameters.signer = signer

  parameters.signRequestsUsingAuthorizationHeader(transport)

  def getFollowingRedirect(url: GenericUrl): HttpResponse = {
    val request = transport.buildGetRequest
    request.url = url
    executeFollowingRedirect(request)
  }

  private def executeFollowingRedirect(request: HttpRequest): HttpResponse = 
    try request.execute
    catch { case e: HttpResponseException =>
      debug("Redirecting...")
      if (e.response.statusCode != 302) throw e
      request.url =  new GenericUrl(e.response.headers.location)
      request.execute
    }

  def postFollowingRedirect(url: GenericUrl, content: HttpContent): HttpResponse = {
    val request = transport.buildPostRequest
    request.url = url
    request.content = content
    executeFollowingRedirect(request)
  }

  def deleteUrl(url: GenericUrl) { 
    val request = transport.buildDeleteRequest
    request.url = url
    request.headers.ifMatch = "*"
    request.execute
  }

}
