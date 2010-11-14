package com.example

import scala.xml._
import org.scalatra._

class FlicksScalatraFilter extends ScalatraFilter {

  object Template {

    def style() =    // """ is scala notation to indicate a string that spans over several lines, including the /n etc
      """
      |body { font-family: Trebuchet MS, sans-serif; }
      |h1 { color: #053B56 }
      |div.date { color: gray; font-size=small; font-style=italic; }
      |div.comment { border: 1px solid gray; width: 40em; }
      """.stripMargin

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
        { for (film <- FilmDatabase.allFilms) yield <li>
            <a href={ "/film/" + film.id }>{ film.title }</a> 
            (<a href={ film.imdbLinkOrSearch } target="_blank">IMDB</a>)
          </li> 
        }
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

  get("/film/:id") {
    val id = params("id")
    val film = FilmDatabase.getFilm(id)
    Template.page(film.title,
      <div>
        <form action={ "/film/" + id } method="POST">
          <div><a href={ film.imdbLinkOrSearch } target="_blank">IMDB-Link</a>: <input type="text" name="imdb" value={ film.imdbLink }/></div>
          <input type="submit" value="Update"/>
        </form>
        <div>Comments:</div>          
        { for (comment <- film.comments) yield 
          <div class="comment">
            <div class="date">{ comment.created }</div>
            <div>{ comment.text }</div>
          </div>
        }
        <form action={ "/film/" + id + "/comment"} method="POST">
          <div><textarea cols="20" rows="5" name="comment"/></div>
          <input type="submit" value="New"/>
        </form>
      </div>)
  }

  post("/film/:id") {
    val id = params("id")
    FilmDatabase.updateFilm(id, params("imdb"))
    redirect("/film/" + id)
  }

  post("/film/:id/comment") {
    val id = params("id")
    FilmDatabase.addCommentToFilm(id, params("comment"))
    redirect("/film/" + id)
  }

}
