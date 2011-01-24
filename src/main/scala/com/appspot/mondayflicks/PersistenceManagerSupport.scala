package com.appspot.mondayflicks

import javax.jdo.{JDOHelper, PersistenceManagerFactory, PersistenceManager}

trait PersistenceManagerSupport {
  protected def withPersistenceManager[T](f: PersistenceManager => T): T = {
    val pm = PersistenceManagerSupport.pmInstance.getPersistenceManager
    try {
      f(pm)
    } finally {
      pm.close
    }
  }
}

object PersistenceManagerSupport {
  private lazy val pmInstance = JDOHelper getPersistenceManagerFactory("transactions-optional")
}
