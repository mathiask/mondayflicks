package com.example

import scala.xml._
import org.scalatra._

class FlicksScalatraFilter extends ScalatraFilter {

  object Template {

    def style() =
      """
      |body { 
      |  font-family: Trebuchet MS, sans-serif; 
      |  padding-left: 100px; padding-right: 100px; padding-top: 50px;
      |}
      |h1 { color: #005580; }
      |a { color: #404040; }
      |a:hover, .editable:hover { background-color: #8ECAE8; }
      |div.date { color: gray; font-size: small; font-style: italic; }
      |div.comment { border: 1px solid #005580;; width: 40em; margin-bottom: 2ex; }
      """.stripMargin

    def page(title:String, content:Seq[Node], message:Option[Any] = None, jQuery:Boolean = false) = {
      <html>
        <head>
          <title>{ title }</title>
          <style>{ Template.style }</style>
          { if (jQuery)
              <script src="http://www.google.com/jsapi"></script>
              <script>google.load('jquery', '1.4.4');</script>
              <script src="/static/jquery.editable.js"></script>
          }
        </head>
        <body>
          <h1>{ title }</h1>
          { content }
          <hr/>
          { message.getOrElse("") }
          { if (message.isDefined) <hr/> }
          <a href="/flicks">Overview</a>
        </body>
      </html>
    }
  }

  private val startPage = "/flicks"

  get("/") { redirect(startPage) }

  get(startPage) {
    Template.page("Monday Flicks", 
      <h2>Overview</h2>
      <ul>
        { for (film <- FilmDatabase.allFilms) yield <li>
            <a href={ "/film/" + film.id }>{ film.title }</a> 
            (<a href={ film.imdbLinkOrSearch } target="_blank">IMDB</a>)
          </li> 
        }
      </ul>
      <form action="/film" method="POST">
        <h2>Create new film entry</h2>
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
    Template.page("Film Details",
      <h2><span id="filmTitle">{ film.title }</span></h2>
      <div>
        <form action={ "/film/" + id } method="POST">
          <a href={ film.imdbLinkOrSearch } target="_blank">IMDB-Link</a>: <input type="text" name="imdb" size="40" value={ film.imdbLink }/>
          <input type="submit" value="Update"/>
        </form>
        <h2>Comments</h2>
        { for (comment <- film.comments) yield 
          <div>
            <div class="date">{ comment.created }</div>
            <div class="comment">{ comment.text }</div>
          </div>
        }
        <form action={ "/film/" + id + "/comment"} method="POST">
          <div><textarea cols="40" rows="5" name="comment"/></div>
          <input type="submit" value="New"/>
        </form>
        <script>
          $('#filmTitle').editable(function(txt) {{
            var trimmedText = txt.trim();
            if (trimmedText !== '') {{
              $.post('{ "/film/" + id + "/rename" }', {{title: txt}}); 
              return true;
            }} else {{
              return false;
            }}
          }});
        </script>
      </div>,
      jQuery = true)
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

  post("/film/:id/rename") {
    FilmDatabase.renameFilm(params("id"), params("title"))
  }
}
