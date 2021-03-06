package com.appspot.mondayflicks

import util.{DateOnly, NonEmailNickname}

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

import java.util.{Date, Calendar}

import javax.jdo.annotations._

import com.google.appengine.api.datastore.{Key, KeyFactory}
import com.google.appengine.api.users.User

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
class Film extends NonEmailNickname {

  import Film.imdbURL

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
  @Persistent var calendarId: String = _

  def id: Long = key.getId

  def imdbLink = if (imdbId == null) "" else imdbLinkForId
  private def imdbLinkForId = imdbURL + "/title/tt" + imdbId + "/"
  def imdbLink_=(url: String) {
    val re = ".*?([0-9]+).*?".r
    imdbId = url match {
      case re(id) if !id.isEmpty => id
      case _ => null
    }
  }
  def imdbLinkOrSearch  = if (imdbId == null) imdbURL + "/find?s=all&q=" + title else imdbLinkForId
  def hasImdbLink = imdbId != null

  def userNickname = nonEmailNickname(user)

  def comments: List[FilmComment]  = commentList.toList
  def add(comment: FilmComment) = commentList.add(comment)

  def isScheduled = scheduledFor != null
  def scheduled = {assert(isScheduled); DateOnly(scheduledFor)}
  def scheduledOption = if (isScheduled) Some(scheduled) else None
  def scheduled_=(date: DateOnly) = scheduledFor = date.toDate
  def unschedule { 
    scheduledFor = null
    calendarId = null
  }


  def isPast = isScheduled && scheduled.isBeforeToday

  def isInCalendar = calendarId != null

  /** Has this film not been scheduled, yet,
   *  and has it been created or has a comment been added in the last 24 hours.
   */
  def isNew = ! isScheduled && recentlyChanged

  private def recentlyChanged = {
    val now = Calendar getInstance
    val cal: Calendar = Calendar.getInstance()
    cal setTime (if (commentList isEmpty) created else commentList.last.created)
    cal.add(Calendar.DATE, 1)
    (cal compareTo now) > 0
  }
}


object Film {
  def apply(title: String, user:User): Film = {
    val film = new Film
    film.user = user
    film.created = new Date
    film.title = title
    film
  }

  val imdbURL = "http://akas.imdb.com"
}


@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
class FilmComment extends NonEmailNickname {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  var key: Key = _

  @Persistent var user: User = _
  @Persistent var created: Date = _
  @Persistent var text: String = _

  def userNickname = nonEmailNickname(user)
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
