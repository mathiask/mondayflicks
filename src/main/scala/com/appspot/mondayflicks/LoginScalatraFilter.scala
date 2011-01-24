package com.appspot.mondayflicks

import org.scalatra.ScalatraFilter
import com.google.appengine.api.users._
import scala.xml.NodeSeq

class LoginScalatraFilter extends ScalatraFilter
with Style with UserSupport with util.Logging {
  val startPage = "/flicks"

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

  get("/login") {
    val next = params.getOrElse("next", startPage)
    page("Login",
    <xml:group>
      <h2>Google</h2>
      <div><a href={loginURL(next)}>Log in with Google account</a></div>
      <h2>Custom</h2>
      <div><form action="/login/login" method="post">
        <input type="hidden" name="next" value={next}/>
        <div><div class="label">Email</div><input type="text" name="email"/>@capgemini.com</div>
        <div><div class="label">Password</div><input type="password" name="pwd"/><input type="submit" value="Log in"/></div>
      </form></div>
    </xml:group>)
  }

  // FIXME
  // 1. move to class
  // 2. DB and all that...
  // 3. SSL?
  post("/login/login") {
    val email = params('email)
    val password = params('pwd)
    if (CGUserDatabase.checkPassword(email, password)) {
      session('user) = new User(email, "local")
      info("User " + email + " logged in.");
      redirect(params('next))
    } else {
      warn("Wrong password for " + email)
      // FIXME flash
      redirect("/login")
    }
  }

  get("/login/logout") {
    session.remove("user")
    page("Logout", <xml:group>You have been logged out.</xml:group>)
  }

  get("/login/admin/users") {
    page("Manage Users", <form action="/login/admin/user" method="post">
           <div><div class="label">Email</div><input type="text" name="email"/>@capgemini.com</div>
           <div><div class="label">Password</div><input type="password" name="pwd"/><input type="submit" value="Create"/></div>
         </form>)
  }

  post("/login/admin/user") {
    CGUserDatabase.addUser(params('email), params('pwd))
    redirect("/login/admin/users")
  }

}
