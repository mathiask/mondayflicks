package com.appspot.mondayflicks

import javax.jdo.{JDOHelper, PersistenceManagerFactory, PersistenceManager}

trait PersistenceManagerSupport {
  private lazy val pmInstance = JDOHelper getPersistenceManagerFactory("transactions-optional")

  protected def withPersistenceManager[T](f: PersistenceManager => T): T = {
    val pm = pmInstance.getPersistenceManager
    try {
      f(pm)
    } finally {
      pm.close
    }
  }
}
