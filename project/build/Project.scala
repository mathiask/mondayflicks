import sbt._

class AppengineTestProject(info: ProjectInfo) extends AppengineProject(info) with DataNucleus {

  val scalatraVersion = "2.0.0-SNAPSHOT"
  val scalatra = "org.scalatra" %% "scalatra" % scalatraVersion

  val scalatraTest = "org.scalatra" %% "scalatra-scalatest" % scalatraVersion % "test"
  val scalatest = "org.scalatest" % "scalatest" % "1.2" % "test"

  val sonatypeNexusSnapshots = "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

}
