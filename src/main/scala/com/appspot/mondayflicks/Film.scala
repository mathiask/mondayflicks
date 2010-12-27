package com.appspot.mondayflicks

import util.DateOnly

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

import java.util.Date

import javax.jdo.annotations._

import com.google.appengine.api.datastore.{Key, KeyFactory}
import com.google.appengine.api.users.User

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
class Film {

  @PrimaryKey 
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  var key: Key = _
  @Persistent var title: String = _
  @Persistent private var imdbId: String = _
  @Persistent var user: User = _
  @Persistent var created: Date = _
  @Persistent private var scheduledFor: Date = _
  @Persistent 
  @Order(extensions = Array(new Extension(vendorName = "datanucleus", key = "list-ordering", value = "created")))
  private var commentList: java.util.List[FilmComment] = _

  def id: Long = key.getId

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
  def hasImdbLink = imdbId != null

  def userNickname = user.getNickname

  def comments: List[FilmComment]  = commentList.toList
  def add(comment: FilmComment) = commentList.add(comment)

  def isScheduled = scheduledFor != null
  def scheduled = {assert(isScheduled); DateOnly(scheduledFor)}
  def scheduledOption = if (isScheduled) Some(scheduled) else None
  def scheduled_=(date: DateOnly) = scheduledFor = date.toDate 

  def isPast = isScheduled && scheduled.isBeforeToday
}


object Film {
  def apply(title: String, user:User): Film = {
    val film = new Film
    film.user = user
    film.created = new Date
    film.title = title
    film
  }
}


@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
class FilmComment {
  @PrimaryKey 
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  var key: Key = _

  @Persistent var user: User = _
  @Persistent var created: Date = _
  @Persistent var text: String = _

  def userNickname = user.getNickname
  def keyString = KeyFactory.keyToString(key)
}

object FilmComment {
  def apply(user: User, text: String) = {
    val comment = new FilmComment
    comment.user = user
    comment.created = new Date
    comment.text = text
    comment
  }
}
