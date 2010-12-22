package com.appspot.mondayflicks.util

import java.util.Date
import java.util.logging.{Logger, Level, Formatter, LogRecord}
import java.text.SimpleDateFormat
import Level._

/** A thin wrapper around JDK logging. */
trait Logging {
  val logger: Logger

  def getLogger(clz: Class[_]): Logger = getLogger(clz.getName)
  def getLogger(name: String):Logger = Logger getLogger name

  def warn(msg: => String) = if (logger isLoggable WARNING) logger.warning(msg)
  def info(msg: => String) = if (logger isLoggable INFO) logger.info(msg)
  def debug(msg: => String) = if (logger isLoggable FINE) logger.fine(msg)
}


class SingleLineFormatter extends Formatter {

  private val dateFormat = new SimpleDateFormat("yyyyMMdd:HHmmss.SSS")

  override def format(record: LogRecord) = 
    dateFormat.format(new Date(record.getMillis)) + " " + 
    record.getLevel + " " +
    record.getLoggerName + " " + 
    record.getMessage

}
