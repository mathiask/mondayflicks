import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val appenginePlugin = "net.stbbs.yasushi" % "sbt-appengine-plugin" % "2.2"
  // get from https://github.com/Yasushi/sbt-appengine-plugin
  // and "publish-local" from sbt
}
