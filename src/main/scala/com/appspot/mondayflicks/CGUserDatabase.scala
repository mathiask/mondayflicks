package com.appspot.mondayflicks

import scala.collection.JavaConversions._

import javax.jdo.annotations._
import javax.jdo.{JDOObjectNotFoundException, PersistenceManager}

/** I am responsible for the custom log in. */
object CGUserDatabase extends PersistenceManagerSupport {

  // TODO: sha1

  def persist(email: String, password: String) = 
    withUser(email) {
      case Some(user) => user.passwordShaBase64 = password
      case None => withPersistenceManager(_.makePersistent(CGUser(email, password)))
    }

  def withUser[T](email: String)(f: Option[CGUser] => T): T = { 
    withPersistenceManager{ pm =>
      try f(Some(doGetUser(pm, email)))
      catch {
        case _: JDOObjectNotFoundException => f(None)
      }
    }
  }

  private def doGetUser(pm: PersistenceManager, email: String) =
    pm.getObjectById(classOf[CGUser], email).asInstanceOf[CGUser]

  def checkPassword(email: String, password: String): Boolean = 
    withUser(email) {
      case Some(user) => user.passwordShaBase64 == password
      case None => false
    }

  def allUsers: Seq[CGUser] =
    withPersistenceManager(pm => 
      pm.newQuery("select from " + classOf[CGUser].getName + " order by email")
      .execute
      .asInstanceOf[java.util.List[CGUser]]
      .map(pm.detachCopy(_)))

  def delete(email: String) {
    withPersistenceManager(pm => pm.deletePersistent(doGetUser(pm, email)))
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
