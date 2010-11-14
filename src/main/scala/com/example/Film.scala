package com.example

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

import java.util.Date

import javax.jdo.annotations._

import com.google.appengine.api.datastore.Key

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
class Film {

  @PrimaryKey 
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  var key: Key = _

  def id: Long = key.getId

  @Persistent var title: String = _

  @Persistent private var imdbId: String = _
  
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
  @Order(extensions = Array(new Extension(vendorName = "datanucleus", key = "list-ordering", value = "created")))
  private var commentList: java.util.List[FilmComment] = _

  def comments: List[FilmComment]  = commentList.toList

  def add(comment: FilmComment) = commentList.add(comment)

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

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
class FilmComment {
  @PrimaryKey 
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  var key: Key = _

  @Persistent var user: String = _
  @Persistent var created: Date = _
  @Persistent var text: String = _
}

object FilmComment {
  def apply(user: String, text: String) = {
    val comment = new FilmComment
    comment.user = user
    comment.created = new Date
    comment.text = text
    comment
  }
}
