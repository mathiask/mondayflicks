package com.example

import scala.collection.mutable.ListBuffer

import javax.jdo.annotations._

import com.google.appengine.api.datastore.Key

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
class Film {

  @PrimaryKey 
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  var key: Key = _

  def id: Long = key.getId

  @Persistent
  var title: String = _

  @Persistent
  private var imdbId: String = _
  
  def imdbLink = if (imdbId == null) "" else imdbLinkForId

  private def imdbLinkForId = "http://www.imdb.com/title/tt" + imdbId + "/"

  def imdbLink_=(url: String) {
    val re = ".*?([0-9]+).*?".r
    imdbId = url match {
      case re(id) if !id.isEmpty => id
      case _ => null
    }
  }

  def imdbLinkOrSearch  = if (imdbId == null) "http://www.imdb.com/find?s=all&q=" + title else imdbLinkForId

  @Persistent
  var comments: String = _

  // val comments = ListBuffer.empty[String] 
  // def addComment(comment: String) { comments += comment }
 
}

object Film {
  def apply(title: String): Film = {
    val film = new Film
    film.title = title
    film
  }
}
