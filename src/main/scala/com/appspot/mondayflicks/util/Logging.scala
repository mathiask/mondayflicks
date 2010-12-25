package com.appspot.mondayflicks.util

import java.util.logging.{Logger, Level}
import Level._

/** A thin wrapper around JDK logging. */
trait Logging {
  lazy val logger = Logger getLogger (getClass.getName)

  def warn(msg: => String) = if (logger isLoggable WARNING) log(WARNING, msg)
  def info(msg: => String) = if (logger isLoggable INFO) log(INFO, msg)
  def debug(msg: => String) = if (logger isLoggable FINE) log(FINE, msg)

  private def log(level: Level, msg: String) = logger.logp(level, null, null, msg)
}
