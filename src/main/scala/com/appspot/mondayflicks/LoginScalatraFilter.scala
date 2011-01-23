package com.appspot.mondayflicks

import org.scalatra.ScalatraFilter

class LoginScalatraFilter extends ScalatraFilter
with Style with UserSupport with util.Logging {
  val startPage = "/flicks"

  get("/login") {
    val doc =
      <html>
        <head>
          <title>Login</title>
          <style>{ style }</style>
        </head>
        <body>
          <div id="main">
            <h1>Login</h1>
            <div>Please log in:</div>
            <div><a href={loginURL(startPage)}>Using Google account</a></div>
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
}
