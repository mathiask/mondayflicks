package com.example

import scala.xml._
import org.scalatra._

class FlicksScalatraFilter extends ScalatraFilter {

  object Template {

    def style() =    // """ is scala notation to indicate a string that spans over several lines, including the /n etc
      """
      body { font-family: Trebuchet MS, sans-serif; }
      h1 { color: #8b2323 }
      """

    def page(title:String, content:Seq[Node], message:Option[Any] = None) = {
      <html>
        <head>
          <title>{ title }</title>
          <style>{ Template.style }</style>
        </head>
        <body>
          <h1>{ title }</h1>
          { content }
          <hr/>
          {  message.getOrElse("") }
          <hr/>
          <a href="/flicks">Overview</a>
        </body>
      </html>
    }
  }

  private val startPage = "/flicks"

  get("/") { redirect(startPage) }

  get(startPage) {
    Template.page("Monday Flicks", 
      <ul>
        { for (film <- FilmDatabase.allFilms) yield <li>{ film.title }</li> }
      </ul>
      <form action="/film" method="POST">
        <input type="text" name="film"/>
        <input type="submit" value="New"/>
      </form>
     )
  }

  post("/film") {
    FilmDatabase.addFilm(params("film"))
    redirect(startPage)
  }

}
