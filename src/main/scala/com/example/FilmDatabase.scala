package com.example

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

import javax.jdo.{JDOHelper, PersistenceManagerFactory, PersistenceManager}

import com.google.appengine.api.datastore.{Key, KeyFactory}

object FilmDatabase {

  private lazy val pmInstance = JDOHelper getPersistenceManagerFactory("transactions-optional")

  def allFilms: Seq[Film] = 
    withPersistenceManager(pm => pm.newQuery("select from " + classOf[Film].getName).execute.
                           asInstanceOf[java.util.List[Film]].map(pm.detachCopy(_)))

  private def withPersistenceManager[T](f: PersistenceManager => T): T = {
    val pm = pmInstance.getPersistenceManager
    try {
      f(pm)
    } finally {
      pm.close
    }
  }

  def addFilm(title: String) { 
    withPersistenceManager(_.makePersistent(Film(title)))
  }

  // def contains(title: String) : Boolean = films.exists(title == _.title)

  def getFilm(id: String) = withPersistenceManager(_.getObjectById(classOf[Film], filmKey(id)).asInstanceOf[Film])

  private def filmKey(id: String): Key = KeyFactory.createKey(classOf[Film].getSimpleName, id.toLong)

  def updateFilm(id: String, imdbLink: String, comments: String) {
    withPersistenceManager((pm:PersistenceManager) => {
      println("Updating film " + id + " to " + imdbLink + " and " + comments)
      val film = pm.getObjectById(classOf[Film], filmKey(id)).asInstanceOf[Film]
      film.imdbLink = imdbLink
      film.comments = comments
    })
  }

  // def addComment(id: Int, comment: String) {
  //   getFilm(id).addComment(comment)
  // }

}
