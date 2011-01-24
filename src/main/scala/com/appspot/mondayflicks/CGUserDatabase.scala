package com.appspot.mondayflicks

import javax.jdo.annotations._
import javax.jdo.JDOObjectNotFoundException

/** I am responsible for the custom log in. */
object CGUserDatabase extends PersistenceManagerSupport {

  // TODO: sha1

  def persistUser(email: String, password: String) = 
    withUser(email) {
      case Some(user) => user.passwordShaBase64 = password
      case None => withPersistenceManager(_.makePersistent(CGUser(email, password)))
    }

  def withUser[T](email: String)(f: Option[CGUser] => T): T = { 
    withPersistenceManager{ pm =>
      try 
        f(Some(pm.getObjectById(classOf[CGUser], email).asInstanceOf[CGUser]))
      catch {
        case _: JDOObjectNotFoundException => f(None)
      }
    }
  }

  def checkPassword(email: String, password: String): Boolean = 
    withUser(email) {
      case Some(user) => user.passwordShaBase64 == password
      case None => false
    }
}

@PersistenceCapable(identityType = IdentityType.APPLICATION)
class CGUser {
  @PrimaryKey var email: String = _
  @Persistent var passwordShaBase64: String = _
}

object CGUser {
  def apply(email: String, passwordShaBase64: String): CGUser = {
    val user = new CGUser
    user.email = email
    user.passwordShaBase64 = passwordShaBase64
    user
  }
}
