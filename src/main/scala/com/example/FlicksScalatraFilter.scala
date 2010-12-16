package com.example

import java.text.SimpleDateFormat
import java.util.Date

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

    def page(title:String, content:NodeSeq, message:Option[Any] = None, scripts:List[String] = Nil) = {
      <html>
        <head>
          <title>{ title }</title>
          <style>{ Template.style }</style>
          <link rel="stylesheet" href="/static/jquery-ui-1.8.7.custom.css" type="text/css" media="all" />
          <script src="http://www.google.com/jsapi"></script>
          <script>google.load('jquery', '1.4.4');</script>
          <script src="/static/jquery.editable.js"></script>
          { for (script <- scripts) yield <script src={ "/static/" + script }></script> }
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
  private def dateFormat = new SimpleDateFormat("yyyy-MM-dd")

  private def currentUser = userService.getCurrentUser
  private def isLoggedIn = request.getUserPrincipal != null
  private def isAdmin = request.isUserInRole("admin")
  private def thisURL = request.getRequestURI


  get("/") { redirect(startPage) }

  get(startPage) {
    Template.page("Monday Flicks", 
                  Seq(<h2>Overview</h2>,
                      filmList, 
                      newFilmForm))
  }

  private def filmList = 
    <ul>
      { for (film <- FilmDatabase.allFilms) yield <li>
          <a href={ "/film/" + film.id }>&#8220;{ film.title }&#8221;</a>
          (<a href={ film.imdbLinkOrSearch } target="_blank">IMDB</a>)
          { if (film.isScheduled) <span>: scheduled for <b>{ dateFormat.format(film.scheduledFor) }</b></span> }
        </li>
      }
    </ul>

  private def newFilmForm = 
    if (isLoggedIn) 
      <form action="/user/film" method="POST">
        <h2>Create new film entry</h2>
        <input id="film" type="text" name="film"/>
        <input id="new" type="submit" value="New"/>
        { onClickNonBlankScript("#new", "#film") }
      </form>
    else <span/>

  /** Add an onclick handler to a control cheking that an input widget is non blank. */
  private def onClickNonBlankScript(controlSelector:String , inputSelector: String) =
    <script>
      $(function(){{
        $('{controlSelector}').click(function(){{return $.trim($('{inputSelector}').val()) !== ''; }});
      }});
    </script>

  post("/user/film") {
    FilmDatabase.addFilm(params('film), currentUser)
    redirect(startPage)
  }

  get("/film/:id") {
    val id = params('id)
    val film = FilmDatabase.getFilm(id)
    Template.page("Film Details", 
                  Seq(<h2><span id="filmTitle">{ film.title }</span></h2>,
                      renameFilmScript(id),
                      filmTitleAndImdbForm(id, film),
                      deleteFilmForm(id),
                      scheduleFilmForm(id, film.scheduledOption),
                      <div class="user">Added by {film.userNickname} on {film.created}.</div>,
                      <h2>Comments</h2>,
                      comments(id, film.comments),
                      addCommentForm(id)),
                  scripts = List("jquery-ui-1.8.7.custom.min.js"))
  }

  private def renameFilmScript(id: String) = 
    <script>
      mondayflicks.renameFile = function(txt) {{
        var trimmedText = $.trim(txt);
        if (trimmedText !== '') {{
          $.post('{ "/user/film/" + id + "/rename" }', {{title: txt}});
          return true;
        }} else {{
          return false;
        }}
      }};
      { if (isLoggedIn) "$('#filmTitle').editable(mondayflicks.renameFile);" }
    </script>

  private def filmTitleAndImdbForm(id: String, film: Film) =
    <form action={ "/user/film/" + id } method="POST">
      <a href={ film.imdbLinkOrSearch } target="_blank">IMDB-Link</a>
      { imdbForm(film.imdbLink) }
    </form>

  private def imdbForm(imdbLink: String) = 
    if (isLoggedIn) Seq(<input id="text" type="text" name="imdb" size="40" value={ imdbLink }/>,
                        <input id="update" type="submit" value="Update"/>,
                        showUpdateButtonScript)
    else Seq()

  private def showUpdateButtonScript =
    <script>
      $(function(){{
        $('#text').hide().data('visible', false);
        $('#update').click(function(){{
          var text = $('#text');
          if (!text.data('visible')) {{ text.show().data('visible', true); return false; }}
          else return $.trim(text.val()) !== '';
        }});
      }});
    </script>

  private def deleteFilmForm(id: String) =
    if (isAdmin) 
      <form action={ "/admin/film/" + id + "/delete" } method="POST">
        <input type="submit" value="Delete" onclick="return confirm('Please confirm!');"/>         
      </form>
    else <span/>

  private def scheduleFilmForm(id: String, scheduled: Option[Date]) = 
    if (scheduled.isDefined || isAdmin) {
      val dateString = scheduled.flatMap(o => Some(dateFormat format o)).getOrElse("")
      <form action={ "/admin/film/" + id + "/schedule" } method="POST">Scheduled for 
        { if (isAdmin) {
            <input id="scheduledFor" type="text" name="scheduledFor" value={ dateString }/>
            <input type="submit" value="Change" onclick="return !!$.trim($('#scheduledFor').val()).match(/^\d{4}-\d{2}-\d{2}$/);"/>
            <script>
              $(function(){{ $('#scheduledFor').datepicker({{firstDay: 1, dateFormat: 'yy-mm-dd'}}); }});
            </script>
          } else {
            <span>{ dateString }</span>
          }
        }
      </form>
    } else <div><em>As yet unscheduled.</em></div>
    
  
  private def comments(id: String, comments: Seq[FilmComment]) =
    <div>
      { for (comment <- comments) yield
        <div>
          <div class="user">{ comment.userNickname }, { comment.created }</div>
          <div class="comment">{ comment.text }</div>
          { deleteCommentForm(id, comment.keyString, comment.user) }
        </div>
      }
    </div>
     
  private def deleteCommentForm(id: String, keyString: String, user: User) =
    if (isAdmin || currentUser == user) 
      <form action={ "/user/film/" + id + "/" + keyString + "/delete" } method="POST">
        <input type="submit" value="Delete" onclick="return confirm('Please confirm!');"/>         
      </form>
    else <span/>

  private def addCommentForm(id: String) =
    if (isLoggedIn)
      <form action={ "/user/film/" + id + "/comment"} method="POST">
        <div><textarea id="comment" cols="40" rows="5" name="comment"/></div>
        <input id="new" type="submit" value="Add comment"/>
        { onClickNonBlankScript("#new", "#comment") }
      </form>
    else <span/>

  post("/user/film/:id") {
    val id = params('id)
    FilmDatabase.updateFilm(id, params("imdb"))
    redirect("/film/" + id)
  }

  post("/user/film/:id/comment") {
    val id = params('id)
    FilmDatabase.addCommentToFilm(id, params("comment"), currentUser)
    redirect("/film/" + id)
  }

  post("/user/film/:id/rename") {
    FilmDatabase.renameFilm(params('id), params('title))
  }

  post("/admin/film/:id/delete") {
    FilmDatabase.deleteFilm(params('id))
    redirect(startPage)
  }

  post("/user/film/:id/:key/delete") {
    FilmDatabase.deleteComment(params('key), currentUser, isAdmin)
    redirect("/film/" + params('id))
  }

  post("/admin/film/:id/schedule") {
    FilmDatabase.schedule(params('id), dateFormat.parse(params('scheduledFor)))
    redirect("/film/" + params('id))
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
