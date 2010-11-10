package com.example

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

import javax.jdo.{JDOHelper, PersistenceManagerFactory, PersistenceManager}

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

  // def getFilm(id: Int) = films.find(_.id == id).get

  // def addComment(id: Int, comment: String) {
  //   getFilm(id).addComment(comment)
  // }

}
