package com.example

import scala.collection.mutable.ListBuffer

object FilmDatabase {

  private val films = ListBuffer(Film(1, "Zelig"), Film(2, "Manhattan"))

  def allFilms = films

  def addFilm(title: String) { films += Film(nextId, title) }

  private def nextId = films.size + 1

  def contains(title: String) : Boolean = films.exists(title == _.title)

  def getFilm(id: Int) = films.find(_.id == id).get

  def addComment(id: Int, comment: String) {
    getFilm(id).addComment(comment)
  }
}
