package com.example

import scala.collection.mutable.ListBuffer

import javax.jdo.annotations._

import com.google.appengine.api.datastore.Key

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
class Film {

  @PrimaryKey 
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  var id: Key = _

  @Persistent
  var title: String = _
  
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
