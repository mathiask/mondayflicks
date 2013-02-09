package com.appspot.mondayflicks

import util._

import scala.xml._
import org.scalatra._

import javax.servlet.FilterConfig

import com.google.appengine.api.users.User

class MondayFlicksScalatraFilter extends ScalatraFilter
with Style with Scripts with UserSupport with NonEmailNickname
with CalendarAccessSupport with TweeterSupport
with UrlHelper
with Logging {

  private def page(title:String,
                   content:NodeSeq,
                   sidebar: Option[NodeSeq] = None,
                   scripts:List[String] = Nil) = {
    val doc =
      <html>
        <head>
          <title>{ title }</title>
          <style>{ style }</style>
          <link href="//fonts.googleapis.com/css?family=Tangerine:700" type="text/css" rel="stylesheet" />
          <link rel="stylesheet" href="/static/jquery-ui-1.8.7.custom.css" type="text/css" media="all" />
          <script src="/static/jquery-1.4.4.min.js"></script>
          <script src="/static/jquery.editable.js"></script>
          { for (script <- scripts) yield <script src={ "/static/" + script }></script> }
          <script>if (typeof mondayflick === 'undefined') mondayflicks = {{}}</script>
        </head>
        <body>
          <div id="main">
            { motd }
            { if (sidebar.isDefined) <div class="sidebar">{ sidebar.get }</div> }
            <h1>{ title }</h1>
            <div id="content">{ content }</div>
            <hr/>
            { appengineIcon }
            { if (isLoggedIn) <xml:group>
                <a href={logoutURL} class="login">Log out</a>
                { if (isCustomLoggedIn) <a href={cgChangePasswordURL} class="login">Change password</a> }
                { if (isAdmin) <a href="/admin" class="login">Admin</a> }
              </xml:group>
              else <xml:group>
                <a href={cgLoginURL} class="login">Custom Log In</a>
                <a href={loginURL} class="login">Google Log In</a>
              </xml:group>
            }
            <a href={startPage}>Overview</a>
          </div>
        </body>
      </html>
    contentType = "text/html"
    asXHTMLWithDocType(doc)
  }

  private def motd =
    if (!session.get('MOTDseen).isDefined)
      <xml:group>
        { motdDiv }
        { motdScript }
      </xml:group>

  private def motdDiv =
    <div id="motd" class="motd">
      <span class="tiny"><img id="close_motd" src="/static/images/close.png" class="inline clickable"/> Message of the Day: </span>
      { Unparsed(KeyValueStore.readOrElse("motd", "Have <em>fun</em>!")) }
    </div>

  private def motdScript = <xml:unparsed>
    <script>
      $(function(){ $("#close_motd").click(function(){
        $("#motd").fadeOut();
        $.post("/motd/seen")
      }); });
    </script>
  </xml:unparsed>

  post("/motd/seen") {
    debug("MOTD seen")
    session('MOTDseen) = "yes"
  }

  private val startPage = "/flicks"

  private def tweet(status: String) = tweeter.tweet(status)
  private def baseURL = {
    val url = request.getRequestURL
    url.substring(0, url lastIndexOf request.getRequestURI)
  }
  private def startURL = baseURL + startPage
  private def filmURL(film: Film): String = baseURL + "/film/" + film.id

  error {
    case _: javax.jdo.JDOObjectNotFoundException =>
      page("Illegal Access",
           <div class="error">The film or comment does not (or no longer) exists.</div>
           <div><a href={startPage}>Restart</a></div>)
    case e =>
      severe(e)
      page("Error",
           <div class="error">Internal server error.</div>
           <div><a href={startPage}>Restart</a></div>)
  }

  get("/") {
    info("Redirecting to start page...")
    redirect(startPage)
  }

  get(startPage) {
    val allFilms = FilmDatabase.allFilms
    val proposedFilms = allFilms filter {! _.isScheduled}
    page("Monday Flicks",
         Seq(filmList(<h2><span id="pastFilms" class="clickable">Past Films<span class="tiny"> [Click]</span></span></h2>,
                      allFilms filter {_.isPast}),
             pastFilmsScript,
             filmList(<h2>Scheduled Films</h2>, allFilms filter {f => f.isScheduled && !f.isPast}),
             filmList(<h2>Proposed Films</h2>, proposedFilms),
             if (proposedFilms.nonEmpty && isLoggedIn) doodleButton else <!-- nothing to doodle -->,
             newFilmForm),
         sidebar = Some(defaultPageSidebar))
  }

  private def defaultPageSidebar = {
      <h3>Social</h3>
      <div style="position: relative;"><span>{calendarPopup}<a target="_blank"
          href="https://www.google.com/calendar/embed?src=pvbp2e5h4t4mhigof30lkq5abc@group.calendar.google.com">
          <img class="icon" src="/static/images/google_calendar.png"/>Google Calendar</a></span></div>
      <div style="position: relative;"><span>{twitterPopup}<a target="_blank" href="http://twitter.com/#!/mondayflicks">
          <img class="icon" src="/static/images/twitter.png"/>Twitter</a></span></div>
      <div><a target="_blank" href="https://github.com/mathiask/mondayflicks/issues"><img class="icon" src="/static/images/github.png"/>Issues</a></div>
      <hr/>
      <h3>Film Lists</h3>
      <div><a target="_blank" href={Film.imdbURL + "/chart/top"}>IMDb Top 250</a></div>
      <div><a target="_blank"
              title="25 Movies You Must See Before You Die"
              href="http://en.wikipedia.org/wiki/1001_Movies_You_Must_See_Before_You_Die#25_Movies_You_Must_See_Before_You_Die">25
        Movies You Must See...</a></div>
  }

  private def calendarPopup = <div class="popup" id="calendarPopup">
    <iframe src="https://www.google.com/calendar/embed?showTitle=0&amp;showNav=0&amp;showDate=0&amp;showPrint=0&amp;showTabs=0&amp;showCalendars=0&amp;showTz=0&amp;height=400&amp;wkst=2&amp;bgcolor=%23FFFFFF&amp;src=pvbp2e5h4t4mhigof30lkq5abc%40group.calendar.google.com&amp;color=%23691426&amp;ctz=Europe%2FBerlin&amp;mode=agenda" style="border:solid 1px #777;" width="300" height="300" frameborder="0" scrolling="no"></iframe>
    { popupScript("calendarPopup") }
  </div>

  private def twitterPopup =
    <div class="popup" id="twitterPopup">
      {twitterScript}
      {popupScript("twitterPopup")}
    </div>

  private def filmList(title: NodeSeq, films: Seq[Film]) = {
    if (films.nonEmpty)
      <div>
        { title }
        { for (film <- films) yield <div>
            { if (film.isScheduled) <span class="item"><em>{ film.scheduled }</em></span> }
            <a href={ "/film/" + film.id }>&#8220;{ film.title }&#8221;</a>
            { if (film.hasImdbLink) <span>(<a href={ film.imdbLink } target="_blank">IMDb</a>)</span> }
          </div>
        }
      </div>
    else <!-- no films in category -->
  }

  private def pastFilmsScript =
    <script><xml:unparsed>
      $(function(){
        var trigger = $('#pastFilms'),
            divs = trigger.parent().siblings('div');
        divs.hide();
        trigger.data('visible', false)
        .editableHover()
        .click(function(){
          var visible = !trigger.data('visible');
          trigger.data('visible', visible);
          if (visible) divs.slideDown();
          else divs.slideUp();
        });
      });
    </xml:unparsed></script>

  private def doodleButton =
    <form action="/user/doodle" method="GET">
      <input type="submit" class="flushleft" value="Create Doodle"/>
    </form>

  private def newFilmForm =
    if (isLoggedIn)
      <form action="/user/film" method="POST">
        <h2>Create new film entry</h2>
        <input id="film" type="text" name="film"/>
        <input id="new" type="submit" value="New"/>
        { onClickNonBlankScript("#new", "#film") }
      </form>
    else <span/>

  get("/user/doodle") {
    val films = FilmDatabase.allFilms filter {! _.isScheduled}
    page("Create Doodle",
         <form action="/user/doodle" method="POST">
           {
             for (film <- films) yield <div>
               { checkbox("film", film.id.toString, FilmDatabase isNew film) }
               { film.title }
             </div>
           }
           <input type="submit" class="flushleft" value="Create"/>
         </form>)
  }

  private def checkbox(name: String, value: String, checked: Boolean) = {
    if (checked)
      <input type="checkbox" name={name} value={value} checked="checked"/>
    else
      <input type="checkbox" name={name} value={value}/>
  }

  post("/user/doodle") {
    val films = multiParams("film").map(FilmDatabase.getFilm _)
    redirect(if (films.isEmpty) startPage else doodleWizardUrl(films))
  }

  private def doodleWizardUrl(films: Seq[Film]) = {
    "http://www.doodle.com/polls/wizard.html?" +
    "type=text" +
    "&levels=3" +
    "&title=Monday+Flicks+Film+Poll" +
    "&name=" + urlEncode(nonEmailNickname(currentUser)) +
    "&eMailAddress=" + currentUser.getNickname +
    films.zipWithIndex.map{ case (f,i) =>
      "option" + (i+1) + "=" + urlEncode(f.title + " " + f.imdbLink)
    }.mkString("&", "&", "")
  }

  post("/user/film") {
    val title = params('film)
    FilmDatabase.addFilm(title, currentUser)
    tweet("New film '" + title + "' created. " + startURL)
    redirect(startPage)
  }

  get("/film/:id") {
    val id = params('id)
    val film = FilmDatabase.getFilm(id)
    page("Film Details",
         Seq(filmTitle(film.title),
             renameFilmScript(id),
             imdbForm(id, film),
             scheduleFilmForm(id, film.scheduledOption, film.isInCalendar),
             <div class="user">Added by {film.userNickname} on {film.created}.</div>,
             deleteFilmForm(id),
             <h2>Comments</h2>,
             comments(id, film.comments),
             addCommentForm(id)),
         sidebar = Some(defaultPageSidebar),
         scripts = List("jquery-ui-1.8.7.custom.min.js"))
  }

  private def filmTitle(title: String) =
    <h2><span id="filmTitle">{ title }</span>{ if (isLoggedIn) <span class="tiny"> [Click]</span> }</h2>

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

  private def imdbForm(id: String, film: Film) =
    <form action={ "/user/film/" + id } method="POST">
      <a href={ film.imdbLinkOrSearch } target="_blank">IMDb-Link</a>
      { imdbLinkInput(film.imdbLink) }
    </form>

  private def imdbLinkInput(imdbLink: String) =
    if (isLoggedIn) Seq(<input id="text" type="text" name="imdb" size="40" value={ imdbLink }/>,
                        <input id="update" type="submit" value="Update"/>,
                        showUpdateButtonScript)
    else Seq()

  private def showUpdateButtonScript =
    <script><xml:unparsed>
      $(function(){
        $('#text').hide().data('visible', false);
        $('#update').click(function(){
          var text = $('#text');
          if (!text.data('visible')) { text.show().data('visible', true); return false; }
          else return $.trim(text.val()) !== '';
        });
      });
    </xml:unparsed></script>

  private def scheduleFilmForm(id: String, scheduled: Option[DateOnly], isInCalendar: Boolean) =
    if (scheduled.isDefined || isAdmin) {
      val dateString = scheduled.flatMap(o => Some(o.toString)).getOrElse("")
      <form action={ "/admin/film/" + id + "/schedule" } method="POST">Scheduled for
        { if (isAdmin) {
            <input id="scheduledFor" type="text" name="scheduledFor" value={ dateString }/>
            <input id="schedule" type="submit" value="Schedule" onclick="return !!$.trim($('#scheduledFor').val()).match(/^\d{4}-\d{2}-\d{2}$|^$/);"/>
            <span>{ if (scheduled.isDefined) { <input type="submit" value="Unschedule" onclick="$('#scheduledFor').val('')"/>
                      <span>{ if (!isInCalendar) <b> Not in calendar!</b> }</span>}}</span>
            <script><xml:unparsed>
              $(function(){
                $('#schedule').hide();
                $('#scheduledFor').datepicker({
                  firstDay: 1,
                  dateFormat: 'yy-mm-dd',
                  onSelect: function(){ $('#schedule').click(); }
                });
              });
            </xml:unparsed></script>
          } else {
            <span>{ dateString }</span>
          }
        }
      </form>
    } else <div><em>As yet unscheduled.</em></div>

  private def deleteFilmForm(id: String) =
    if (isAdmin)
      <form action={ "/admin/film/" + id + "/delete" } method="POST">
        <input type="submit" value="Delete film" onclick="return confirm('Please confirm!');"/>
      </form>
    else <span/>

  private def comments(id: String, comments: Seq[FilmComment]) =
    <div>
      { for (comment <- comments) yield
        <div>
          <div class="user">{ comment.userNickname }, { comment.created }</div>
          <div class="comment">
            { deleteCommentForm(id, comment.keyString, comment.user) }
            { comment.text }
          </div>
        </div>
      }
    </div>

  private def deleteCommentForm(id: String, keyString: String, user: User) =
    if (isAdmin || currentUser == user)
      <form action={ "/user/film/" + id + "/" + keyString + "/delete" } method="POST">
        <input type="submit" class="delete" value="Delete" onclick="return confirm('Please confirm!');"/>
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
    FilmDatabase.updateFilm(id, params('imdb))
    redirect("/film/" + id)
  }

  post("/user/film/:id/comment") {
    val id = params('id)
    FilmDatabase.addCommentToFilm(id, params('comment), currentUser)
    val film = FilmDatabase.getFilm(id)
    tweet("Comment for " + filmURL(film) + " " + film.title)
    redirect("/film/" + id)
  }

  post("/user/film/:id/rename") {
    FilmDatabase.withFilm(params('id)) { film =>
      film.title = params('title)
      if (film.isInCalendar) calendar.update(film)
      tweet("Film " + filmURL(film) + " renamed to " + film.title)
    }
  }

  post("/admin/film/:id/delete") {
    val id = params('id)
    val film = FilmDatabase.getFilm(id)
    FilmDatabase.deleteFilm(id)
    calendar.delete(film.calendarId)
    tweet("Film " + film.title + " deleted")
    redirect(startPage)
  }

  post("/user/film/:id/:key/delete") {
    FilmDatabase.deleteComment(params('key), currentUser, isAdmin)
    redirect("/film/" + params('id))
  }

  post("/admin/film/:id/schedule") {
    FilmDatabase.withFilm(params('id)) { film =>
      val scheduledFor = params('scheduledFor).trim
      if (scheduledFor.isEmpty) {
        unschedule(film)
        tweet("Unscheduled " + filmURL(film) + " " + film.title)
      }
      else {
        schedule(film, DateOnly(scheduledFor))
        tweet("Scheduled " + filmURL(film) + " " + film.title + " for " + film.scheduled)
      }
    }
    redirect("/film/" + params('id))
  }

  private def unschedule(film: Film) {
    calendar.delete(film.calendarId)
    film.unschedule
  }

  private def schedule(film: Film, date: DateOnly) {
    film.scheduled = date
    if (film.isInCalendar)
      calendar.update(film)
    else
      film.calendarId = calendar.create(film)
  }

  get("/admin") {
    val motd = KeyValueStore.readOrElse("motd", "")
    page("Administration", <xml:group>
         <div>Calendar backend: { calendar match {
           case _: DummyCalendarAccess => "dummy"
           case _ => "Google"}}</div>
         <div><a href="/admin/calendar">Connect to Google Calendar</a></div>
         <form action="/admin/motd" method="post">
           <p>
             MOTD: <input type="text" name="motd" value={motd}/>
             <input type="submit" value="Set"/>
           </p>
         </form>
         <p><a href="/login/admin/users">User administration</a></p>
         <p><a href="/admin/cache/stats">Memcache statistics</a></p>
      </xml:group>)
  }

  get("/admin/calendar") {
    redirect(oauth2url(calendarTokenPath))
  }

  private val calendarTokenPath = "/admin/calendar/token"

  get(calendarTokenPath) {
    exchangeCodeForToken(params('code), calendarTokenPath)
    redirect("/admin")
  }

  post("/admin/motd") {
    KeyValueStore.set("motd", params('motd))
    redirect("/admin")
  }

  get("/admin/cache/stats") {
    val stats = KeyValueStore.stats
    page("Memcache Statistics",
         <table>
           <tr><th>Items</th><td>{stats.getItemCount}</td></tr>
           <tr><th>Hits:misses</th><td>{stats.getHitCount}:{stats.getMissCount}</td></tr>
           <tr><th>Idle seconds</th><td>{stats.getMaxTimeWithoutAccess}</td></tr>
         </table>)
  }
}
