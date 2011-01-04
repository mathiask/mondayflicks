package com.appspot.mondayflicks.util

import java.lang.Throwable
import java.util.logging.{Logger, Level}
import Level._

/** A thin wrapper around JDK logging. */
trait Logging {
  lazy val logger = Logger getLogger (loggerName)

  protected def loggerName = getClass.getName

  def warn(msg: => Any) = if (logger isLoggable WARNING) log(WARNING, msg)
  def info(msg: => Any) = if (logger isLoggable INFO) log(INFO, msg)
  def debug(msg: => Any) = if (logger isLoggable FINE) log(FINE, msg)
  def error(thrown: Throwable, msg: String = "") =
    logger.logp(SEVERE, null, null, msg, thrown)

  private def log(level: Level, msg: Any) = 
    logger.logp(level, null, null, if (msg == null) null else msg.toString)
}
