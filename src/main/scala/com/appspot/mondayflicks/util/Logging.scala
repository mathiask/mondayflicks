package com.appspot.mondayflicks.util

import java.util.logging.{Logger, Level}
import Level._

/** A thin wrapper around JDK logging. */
trait Logging {
  private val logger = Logger.getLogger(getClass.getName)

  def warn(msg: => String) = if (logger isLoggable WARNING) logger.warning(msg)
  def info(msg: => String) = if (logger isLoggable INFO) logger.info(msg)
  def debug(msg: => String) = if (logger isLoggable FINE) logger.fine(msg)
}
