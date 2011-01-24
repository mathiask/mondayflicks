package com.appspot.mondayflicks

import util.DateOnly

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

import javax.jdo.PersistenceManager

import com.google.appengine.api.datastore.{Key, KeyFactory}
import com.google.appengine.api.users.User


object FilmDatabase extends PersistenceManagerSupport {

  def allFilms: Seq[Film] =
    withPersistenceManager(pm => pm.newQuery("select from " + classOf[Film].getName + " order by scheduledFor, created").
                           execute.asInstanceOf[java.util.List[Film]].map(pm.detachCopy(_)))

  def addFilm(title: String, user: User) {
    withPersistenceManager(_.makePersistent(Film(title, user)))
  }

  def getFilm(id: String) = withPersistenceManager(pm => {
    val film = doGetFilm(pm, id)
    for (c <- film.comments) {}
    film}
  )

  private def doGetFilm(pm: PersistenceManager, id: String) =
    pm.getObjectById(classOf[Film], filmKey(id)).asInstanceOf[Film]

  private def filmKey(id: String): Key = KeyFactory.createKey(classOf[Film].getSimpleName, id.toLong)

  def updateFilm(id: String, imdbLink: String) {
    withPersistenceManager(doGetFilm(_, id).imdbLink = imdbLink)
  }

  def addCommentToFilm(id: String, comment: String, user: User) {
    withPersistenceManager(doGetFilm(_, id).add(FilmComment(user, comment)))
  }

  def deleteFilm(id: String) {
    withPersistenceManager(pm => pm.deletePersistent(doGetFilm(pm, id)))
  }

  def deleteComment(keyString: String, user: User, admin: Boolean) {
    withPersistenceManager(pm => { 
      val comment = pm.getObjectById(classOf[FilmComment], KeyFactory.stringToKey(keyString)).asInstanceOf[FilmComment]
      if (admin || user == comment.user)
        pm.deletePersistent(comment)
    })
  }

  /** Get a film in transaction context. */
  def withFilm(id: String)(callback: Film => Unit) =  
    withPersistenceManager(pm => callback(doGetFilm(pm, id)))

}
