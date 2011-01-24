package com.appspot.mondayflicks

import javax.jdo.annotations._

/** I am responsible for the custom log in. */
object CGUserDatabase extends PersistenceManagerSupport {
  def addUser(email: String, password: String) {
    withPersistenceManager(_.makePersistent(CGUser(email, password))) // FIXME SHA
  }

  def checkPassword(email: String, password: String): Boolean = {
    withPersistenceManager(pm => {
      val user = pm.getObjectById(email).asInstanceOf[CGUser]
      user.passwordShaBase64 == password
    })
  }
}


@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
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
