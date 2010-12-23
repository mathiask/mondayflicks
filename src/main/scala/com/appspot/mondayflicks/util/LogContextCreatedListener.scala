package com.appspot.mondayflicks.util

import javax.servlet.{ServletContextEvent, ServletContextListener}

class LogContextCreatedListener extends ServletContextListener with Logging {
  override def contextInitialized(event: ServletContextEvent) = info("Context initialized.") 
  override def contextDestroyed(event: ServletContextEvent) = ()
}
