import sbt._

class MondayFlicksProject(info: ProjectInfo) extends AppengineProject(info) with DataNucleus {

  val scalatraVersion = "2.0.0-SNAPSHOT"
  val scalatra = "org.scalatra" %% "scalatra" % scalatraVersion

  val servletApi = "org.mortbay.jetty" % "servlet-api" % "2.5-20081211" % "provided"

  val googleApiClient = "com.google.api.client" % "google-api-client" % "1.2.1-alpha"

  // TODO
  // val googleApiClientGoogleapis = "com.google.api.client" % "google-api-client-googleapis" % "1.4.1-beta"
  // val googleApiClientExtendsions = "com.google.api.client" % "google-api-client-extensions" % "1.4.1-beta"

  val scalatraTest = "org.scalatra" %% "scalatra-scalatest" % scalatraVersion % "test"
  val scalatest = "org.scalatest" % "scalatest_2.9.0" % "1.6.1" % "test"

  val sonatypeNexusSnapshots = "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

}
