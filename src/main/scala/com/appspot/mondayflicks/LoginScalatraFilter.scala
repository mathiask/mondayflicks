package com.appspot.mondayflicks

import util._
import org.scalatra.{ScalatraFilter, FlashMapSupport}
import com.google.appengine.api.users._
import scala.xml.NodeSeq

class LoginScalatraFilter extends ScalatraFilter
with Style with Scripts with UserSupport with FlashMapSupport with Logging {
  private val startPage = "/flicks"

  def page(title: String, content: NodeSeq) = {
    val doc =
      <html>
        <head>
          <title>{ title }</title>
          <script src="/static/jquery-1.4.4.min.js"></script>
          <style>{ style }</style>
          <style>div.label{{ width: 6em; font-style: italic; display: inline-block; }}</style>
        </head>
        <body>
          <div id="main">
            <h1>{ title }</h1>
            { message }
            { content }
            <hr/>
            { appengineIcon }
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
    severe(caughtThrowable)
    page("Error",
         <div class="error">Internal server error.</div>
         <div><a href={startPage}>Restart</a></div>)
  }

  get("/login") {
    val next = params.getOrElse('next, startPage)
    page("Login",
    <xml:group>
      <h2>Google</h2>
      <div><a href={loginURL(next)}>Log in with Google account</a></div>
      <h2>Custom</h2>
      <div><form action="/login/login" method="post">
        <input type="hidden" name="next" value={next}/>
        { loginControls("Log in") }
      </form></div>
    </xml:group>)
  }

  private def loginControls(submitLabel: String) = {
    <xml:group>
      <div><div class="label">Email</div><input id="email" type="text" name="email" value={ params.getOrElse('email, "") }/>@capgemini.com</div>
      <div><div class="label">Password</div><input type="password" name="pwd"/></div>
      <div> <input id="submit" type="submit" value={submitLabel} /></div>
      { onClickNonBlankScript("#submit", "#email") }
    </xml:group>
  }

  post("/login/login") {
    val rawEmail = params('email).trim
    val email = normalize(rawEmail)
    if (CGUserDatabase.checkPassword(email, params('pwd).trim)) {
      session('user) = new User(email, "local")
      info("User " + email + " logged in.");
      redirect(params('next))
    } else {
      warn("Wrong password for " + email)
      flash("message") = "Wrong email or password"
      redirect("/login?email=" + rawEmail)
    }
  }

  private def normalize(email: String) = {
    val e = email.trim.toLowerCase
    if (e contains "@") e else e + "@capgemini.com"
  }

  get("/login/user/change") {
    if (!isCustomLoggedIn) {
      warn("Cannot change password for Google log in.")
      redirect(startPage)
    }
    else passwordChangePage
  }

  private def passwordChangePage = {
    val next = params.getOrElse('next, startPage)
    page("Change Password",
         <form action="/login/user/change" method="post">
           <input type="hidden" name="next" value={next}/>
           <div>New password <input id="pwd" type="password" name="pwd"/></div>
           <div> <input id="submit" type="submit" value="Change"/></div>
           { onClickMinLengthScript("#submit", "#pwd", 6, "Password must be at least six characters long.") }
         </form>)
  }

  post("/login/user/change") {
    CGUserDatabase.persist(currentUser.getEmail, params('pwd).trim)
    redirect(params('next))
  }

  get("/login/logout") {
    session.invalidate
    page("Logout", <xml:group>You have been logged out.</xml:group>)
  }

  get("/login/admin") {
    redirect("/login/admin/users")
  }

  get("/login/admin/users") {
    val users = CGUserDatabase.allUsers
    page("Manage Users",
         <xml:group>
           <h2>Create or change user</h2>
           <form action="/login/admin/user" method="post">
           { loginControls("Create/Update") }
           </form>
           <h2>All Users</h2>
           <div id="users">
             { for (user <- users) yield
                 <div>
                   <form action="/login/admin/delete" method="post" class="inline">
                     <input type="hidden" name="email" value={user.email}/>
                     <input type="submit" value="Delete" onclick="return confirm('Please confirm!');"/>
                   </form>
                   <a href="#">{user.email}</a>
                 </div> }
           </div>
           { copyEmailScript }
         </xml:group>)
  }

  private def copyEmailScript =
    <script><xml:unparsed>
      $(function(){
        $("#users a").click(function(){
          $("#email").val($(this).text().replace(/@capgemini.com$/, ""));
        });
      });
    </xml:unparsed></script>

  post("/login/admin/user") {
    CGUserDatabase.persist(normalize(params('email)), params('pwd).trim)
    redirect("/login/admin/users")
  }

  post("/login/admin/delete") {
    CGUserDatabase.delete(params('email).trim)
    redirect("/login/admin/users")
  }

}
