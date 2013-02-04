import sbt._

class MondayFlicksProject(info: ProjectInfo) extends AppengineProject(info) with DataNucleus {

  val scalatraVersion = "2.0.3"
  val scalatra = "org.scalatra" %% "scalatra" % scalatraVersion

  val servletApi = "org.mortbay.jetty" % "servlet-api" % "2.5-20081211" % "provided"

  val googleApiClient = "com.google.api.client" % "google-api-client" % "1.2.1-alpha"

  // Unforunately, they break all the Twitter OAuth code
  // val googleApiServices = "Google API Services" at "http://google-api-client-libraries.appspot.com/mavenrepo"
  // val googleApiServicesCalendar = "com.google.apis" % "google-api-services-calendar" % "v3-rev24-1.13.2-beta"


  val scalatraScalatest = "org.scalatra" %% "scalatra-scalatest" % scalatraVersion % "test"
  val scalatest = "org.scalatest" % "scalatest_2.9.0" % "1.6.1" % "test"

  val sonatypeNexusSnapshots = "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
}
