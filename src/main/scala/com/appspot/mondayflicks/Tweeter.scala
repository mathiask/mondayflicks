package com.appspot.mondayflicks

import util.Logging
import collection.{Map, SortedMap}
import scala.collection.JavaConversions._

import java.net.URLEncoder

import com.google.api.client.http._
import com.google.api.client.auth.oauth._

trait Tweeter {
  def homeTimeline: String
  def tweet(status: String): Unit
}

class DummyTweeter extends Tweeter with Logging {
  def homeTimeline = "dummy timeline"
  def tweet(status: String) = info("Dummy-Tweet: " + status)
}


class TwitterTweeter(consumerKey: String , consumerSecret: String, token: String, tokenSecret: String) 
extends Logging with Tweeter {

  val oauth =
    new OAuthRestResource(token, tokenSecret, consumerKey, consumerSecret)
  val signer = new OAuthHmacSigner
  signer.clientSharedSecret = consumerSecret
  signer.tokenSharedSecret = tokenSecret

  def homeTimeline: String =
    oauth.getFollowingRedirect(new GenericUrl("https://api.twitter.com/1/statuses/home_timeline.json")).parseAsString

  def tweet(status: String) = {
    val oauthParams = createOAuthParameters
    val statusMap = new java.util.HashMap[String, String]
    statusMap.put("status", restrictTo140(status))
    val url = "https://api.twitter.com/1/statuses/update.json"
    val baseString = computeBaseString("POST", url, oauthParams, statusMap)
    debug("Base string: " + baseString) 
    oauthParams.signature = signer computeSignature baseString
    val transport = new HttpTransport
    val request = transport.buildPostRequest
    request.url = new GenericUrl(url)
    val content = new UrlEncodedContent
    content.data = statusMap
    request.content = content
    request.headers.authorization = oauthParams.getAuthorizationHeader
    try request.execute
    catch { // don't fail because of Twitter
      case e => severe(e)
    }
  }

  private def createOAuthParameters = {
    val oauthParams = new OAuthParameters
    oauthParams.consumerKey = consumerKey
    oauthParams.token = token
    oauthParams.version = "1.0"
    oauthParams.signatureMethod = signer.getSignatureMethod
    oauthParams.computeNonce
    oauthParams.computeTimestamp
    oauthParams
  }

  private def restrictTo140(s: String) = 
    if (s.size <= 140) s
    else s.substring(0, 137) + "..."

  private def computeBaseString(method: String, 
                                url: String, 
                                oauthParams: OAuthParameters,
                                statusMap: Map[String, String]) = {
    val requestParams = collection.mutable.Map[String, String]()
    requestParams ++= statusMap
    requestParams ++= oauthParameterMap(oauthParams)
    val sortedMap = SortedMap[String, String]() ++ requestParams
    method + "&" + urlEncode(url) + "&" +
      sortedMap.map(p => p._1 + "%3D" + urlEncode(urlEncode(p._2))).mkString("%26")
  }

  private def oauthParameterMap(oauthParams: OAuthParameters): Map[String, String] = 
    Map("oauth_consumer_key" -> oauthParams.consumerKey,
        "oauth_nonce" -> oauthParams.nonce,
        "oauth_signature_method" -> oauthParams.signatureMethod,
        "oauth_timestamp" -> oauthParams.timestamp,
        "oauth_token" -> oauthParams.token,
        "oauth_version" -> oauthParams.version)

  private def urlEncode(s: String) = 
    URLEncoder.encode(s, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~")

}
