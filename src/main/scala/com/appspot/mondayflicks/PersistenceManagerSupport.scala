package com.appspot.mondayflicks

import javax.jdo.{JDOHelper, PersistenceManagerFactory, PersistenceManager, JDOObjectNotFoundException}

trait PersistenceManagerSupport[E] {
  protected def withPersistenceManager[T](f: PersistenceManager => T): T = {
    val pm = PersistenceManagerSupport.pmInstance.getPersistenceManager
    try {
      f(pm)
    } finally {
      pm.close
    }
  }

  protected def getEntity(pm: PersistenceManager, id: String)(implicit m: Manifest[E]) =
    pm.getObjectById(m.erasure, id).asInstanceOf[E]

  protected def withEntity[T](id: String)(f: Option[E] => T)(implicit m: Manifest[E]): T = { 
    withPersistenceManager{ pm =>
      try f(Some(getEntity(pm, id)(m)))
      catch {
        case _: JDOObjectNotFoundException => f(None)
      }
    }
  }

}

object PersistenceManagerSupport {
  private lazy val pmInstance = JDOHelper getPersistenceManagerFactory("transactions-optional")
}
