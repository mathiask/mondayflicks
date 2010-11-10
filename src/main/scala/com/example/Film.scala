package com.example

import scala.collection.mutable.ListBuffer

case class Film(val id: Int, val title: String) {

  val comments = ListBuffer.empty[String] 

  def addComment(comment: String) { comments += comment }
 
}
