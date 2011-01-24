package com.appspot.mondayflicks

import org.scalatra.{ScalatraFilter, FlashMapSupport}
import com.google.appengine.api.users._
import scala.xml.NodeSeq

class LoginScalatraFilter extends ScalatraFilter
with Style with UserSupport with FlashMapSupport with util.Logging {
  private val startPage = "/flicks"

  def page(title: String, content: NodeSeq) = {
    val doc =
      <html>
        <head>
          <title>{ title }</title>
          <style>{ style }</style>
          <style>div.label{{ width: 6em; font-style: italic; display: inline-block; }}</style>
        </head>
        <body>
          <div id="main">
            <h1>{ title }</h1>
            { message }
            { content }
            <hr/>
            <div class="appengine">
              <a href="http://code.google.com/appengine/" target="_blank">
                <img src="/static/images/appengine-silver-120x30.gif" alt="Powered by Google App Engine" />
              </a>
            </div>
            <a href={startPage}>Back to overview</a>
          </div>
        </body>
      </html>
    contentType = "text/html"
    asXHTMLWithDocType(doc)
  }

  private def message = {
    info(flash)
    if (flash.isDefinedAt("message")) {
      <div class="error">{ flash("message") } </div>
    }
  }

  error {
    error(caughtThrowable)
    page("Error",
         <div class="error">Internal server error.</div>
         <div><a href={startPage}>Restart</a></div>)
  }

  get("/login") {
    val next = params.getOrElse("next", startPage)
    page("Login",
    <xml:group>
      <h2>Google</h2>
      <div><a href={loginURL(next)}>Log in with Google account</a></div>
      <h2>Custom</h2>
      <div><form action="/login/login" method="post">
        <input type="hidden" name="next" value={next}/>
        <div><div class="label">Email</div><input type="text" name="email" value={ params.getOrElse("email", "") }/>@capgemini.com</div>
        <div><div class="label">Password</div><input type="password" name="pwd"/><input type="submit" value="Log in"/></div>
      </form></div>
    </xml:group>)
  }

  // TODO: SSL
  post("/login/login") {
    val email = params('email)
    // TODO: normalize email: append @capgemini.com if no @ present
    val password = params('pwd)
    if (CGUserDatabase.checkPassword(email, password)) {
      session('user) = new User(email, "local")
      info("User " + email + " logged in.");
      redirect(params('next))
    } else {
      warn("Wrong password for " + email)
      flash("message") = "Wrong email or password"
      redirect("/login?email=" + email)
    }
  }

  // TODO: get("/login/change"), forward if not logged in
  // TODO: add link to this page

  get("/login/logout") {
    session.remove("user")
    page("Logout", <xml:group>You have been logged out.</xml:group>)
  }

  get("/login/admin") {
    redirect("/login/admin/users")
  }

  get("/login/admin/users") {
    // TODO: delete, change password
    page("Manage Users", <form action="/login/admin/user" method="post">
           <div><div class="label">Email</div><input type="text" name="email"/>@capgemini.com</div>
           <div><div class="label">Password</div><input type="password" name="pwd"/><input type="submit" value="Create"/></div>
         </form>)
  }

  post("/login/admin/user") {
    CGUserDatabase.persistUser(params('email), params('pwd))
    redirect("/login/admin/users")
  }

}
