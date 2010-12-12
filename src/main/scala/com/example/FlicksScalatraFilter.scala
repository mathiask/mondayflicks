package com.example

import scala.xml._
import org.scalatra._

import com.google.appengine.api.users.User

class FlicksScalatraFilter extends ScalatraFilter {

  object Template {

    def style() =
      """
      |body {
      |  font-family: Trebuchet MS, sans-serif;
      |  padding-left: 30px; padding-right: 30px; padding-top: 30px;
      |  min-width: 50ex; max-width: 100ex;
      |  margin-left: auto; margin-right: auto;
      |  background-color: white;
      |}
      |#main {
      |  background-color: #eeeeee;
      |  border: 1px solid #003b6b;
      |  border-radius: 8px; -moz-border-radius: 8px; -webkit-border-radius: 8px;
      |  padding: 2ex;
      |}
      |h1 { color: #005580; }
      |a { color: #404040; }
      |a:hover, .editable:hover { background-color: #8ECAE8; }
      |input, textarea { border-radius: 4px; -moz-border-radius: 4px; -webkit-border-radius: 4px; }
      |div.user {
      |  color: gray;
      |  font-size: small;
      |  font-style: italic;
      |}
      |div.comment {
      |  padding: 4px;
      |  border: 2px solid #005580;
      |  border-radius: 4px; -moz-border-radius: 4px; -webkit-border-radius: 4px;
      |  width: 40em; margin-bottom: 2ex;
      |}
      |div.appengine, a.login {
      |  float: right;
      |  margin-left: 1ex;
      |}
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
          <script>if (typeof mondayflick === 'undefined') mondayflicks = {{}}</script>
        </head>
        <body>
          <div id="main">
            <h1>{ title }</h1>
            { content }
            <hr/>
            { message.getOrElse("") }
            { if (message.isDefined) <hr/> }
            <div class="appengine">
              <a href="http://code.google.com/appengine/" target="_blank">
                <img src="http://code.google.com/appengine/images/appengine-silver-120x30.gif" alt="Powered by Google App Engine" />
              </a>
            </div>
            <a href={startPage}>Overview</a>
            { if (isLoggedIn) <a href={userService createLogoutURL thisURL} class="login">Log out</a>
              else <a href={userService createLoginURL thisURL} class="login">Log in</a>
            }
          </div>
        </body>
      </html>
    }
  }

  private val startPage = "/flicks"
  private lazy val userService = com.google.appengine.api.users.UserServiceFactory.getUserService

  private def currentUser = userService.getCurrentUser
  private def isLoggedIn = request.getUserPrincipal != null
  private def isAdmin = request.isUserInRole("admin")
  private def thisURL = request.getRequestURI


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
      <div>
      { if (isLoggedIn) 
          <form action="/user/film" method="POST">
            <h2>Create new film entry</h2>
            <input id="film" type="text" name="film"/>
            <input id="new" type="submit" value="New"/>
            <script>
              $(function(){{
                $('#new').click(function(){{return $('#film').val().trim() !== ''; }});
              }});
            </script>
          </form>
      }
      </div>,
      jQuery = true
    )
  }

  post("/user/film") {
    FilmDatabase.addFilm(params("film"), currentUser)
    redirect(startPage)
  }

  get("/film/:id") {
    val id = params("id")
    val film = FilmDatabase.getFilm(id)
    Template.page("Film Details",
      <h2><span id="filmTitle">{ film.title }</span></h2>
      <script>
        mondayflicks.renameFile = function(txt) {{
          var trimmedText = txt.trim();
          if (trimmedText !== '') {{
            $.post('{ "/user/film/" + id + "/rename" }', {{title: txt}});
            return true;
          }} else {{
            return false;
          }}
        }};
        { if (isLoggedIn) "$('#filmTitle').editable(mondayflicks.renameFile);" }
      </script>
      <div>
        <form action={ "/user/film/" + id } method="POST">
          <a href={ film.imdbLinkOrSearch } target="_blank">IMDB-Link</a>
          { if (isLoggedIn) {
              <input id="text" type="text" name="imdb" size="40" value={ film.imdbLink }/>
              <input id="update" type="submit" value="Update"/>
              <script>
                $(function(){{
                  $('#text').hide().data('visible', false);
                  $('#update').click(function(){{
                    var text = $('#text');
                    if (!text.data('visible')) {{ text.show().data('visible', true); return false; }}
                    else return text.val().trim() !== '';
                  }});
                }});
              </script>
          }}
        </form>
        { if (isAdmin) 
            <form action={ "/admin/film/" + id + "/delete" } method="POST">
              <input type="submit" value="Delete" onclick="return confirm('Please confirm!');"/>         
            </form>
        }
        <div class="user">Added by {film.userNickname} on {film.created}.</div>
        <h2>Comments</h2>
        { for (comment <- film.comments) yield
          <div>
            <div class="user">{ comment.userNickname }, { comment.created }</div>
            <div class="comment">{ comment.text }</div>
          </div>
        }
        { if (isLoggedIn) {
          <form action={ "/user/film/" + id + "/comment"} method="POST">
            <div><textarea cols="40" rows="5" name="comment"/></div>
            <input type="submit" value="New"/>
          </form>
        }}
      </div>,
      jQuery = true)
  }

  post("/user/film/:id") {
    val id = params("id")
    FilmDatabase.updateFilm(id, params("imdb"))
    redirect("/film/" + id)
  }

  post("/user/film/:id/comment") {
    val id = params("id")
    FilmDatabase.addCommentToFilm(id, params("comment"), currentUser)
    redirect("/film/" + id)
  }

  post("/user/film/:id/rename") {
    FilmDatabase.renameFilm(params("id"), params("title"))
  }

  post("/admin/film/:id/delete") {
    FilmDatabase.deleteFilm(params("id"))
    redirect(startPage)
  }

  // --------------------------------------------------------------------------------

//   get("/admin/migrate") {
//     for (film <- FilmDatabase.allFilms) FilmDatabase.migrateFilm(film.id.toString, currentUser)
//     redirect(startPage)
//   }

  // --------------------------------------------------------------------------------

  get("/user/principal") {
    val user = currentUser
    request.getUserPrincipal + ", admin: " + isAdmin +
    " / " + user.getNickname + "(" + user.getEmail + ")"
  }

}
