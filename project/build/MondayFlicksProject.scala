import sbt._

class MondayFlicksProject(info: ProjectInfo) extends AppengineProject(info) with DataNucleus {

  val scalatraVersion = "2.0.0-SNAPSHOT"
  val scalatra = "org.scalatra" %% "scalatra" % scalatraVersion
  val googleApiClient = "com.google.api.client" % "google-api-client" % "1.2.1-alpha"

  val scalatraTest = "org.scalatra" %% "scalatra-scalatest" % scalatraVersion % "test"
  val scalatest = "org.scalatest" % "scalatest" % "1.2" % "test"

  val sonatypeNexusSnapshots = "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

}
