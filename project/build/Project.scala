import sbt._

class AppengineTestProject(info: ProjectInfo) extends AppengineProject(info) with DataNucleus {

  val scalatraVersion = "2.0.0-SNAPSHOT"
  val scalatra = "org.scalatra" %% "scalatra" % scalatraVersion

  val sonatypeNexusSnapshots = "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

}
