Monday Flicks
=============

A small Google AppEngine App to plan films to watch.

Technology Stack
----------------
* Google [AppEngine](http://code.google.com/appengine/docs/java/overview.html)
  * using Google account based user service
  * plus custom log in
* [Scala](http://www.scala-lang.org/)
  * using plain [JDO](http://code.google.com/appengine/docs/java/datastore/usingjdo.html)
  * and a minimal trait wrapper around JDK logging
* [Scalatra](https://github.com/scalatra/scalatra)
* [jQuery](http://docs.jquery.com/Main_Page)
* [Google Calendar](https://developers.google.com/google-apps/calendar/) integration
  * using [OAuth 2.0](http://oauth.net/2/) with built in Scala classes
* [Twitter](http://dev.twitter.com/doc) integration
  * using [OAuth](http://tools.ietf.org/html/rfc5849)
  * with an older (alpha) version of [google-api-java-client](http://code.google.com/p/google-api-java-client/) with some extras

TODOs/Ideas
-----------
* Autocompletion when entering new films
