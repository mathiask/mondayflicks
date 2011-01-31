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
* [Google Calendar](http://code.google.com/apis/calendar/data/2.0/developers_guide.html) integration
  * using [google-api-java-client](http://code.google.com/p/google-api-java-client/)
* [Twitter](http://dev.twitter.com/doc) integration
  * also using [google-api-java-client](http://code.google.com/p/google-api-java-client/)
    with some extras

TODOs
-----
* Use memcache
* DIY Atom feeds
