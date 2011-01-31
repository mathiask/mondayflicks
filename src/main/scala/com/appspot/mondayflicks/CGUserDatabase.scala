package com.appspot.mondayflicks

import scala.collection.JavaConversions._

import java.security.{MessageDigest, SecureRandom}

import javax.jdo.annotations._

import com.google.api.client.util.Base64.{encode => base64}

/** I am responsible for the custom log in. */
object CGUserDatabase extends PersistenceManagerSupport[CGUser] {

  def persist(email: String, password: String) = 
    withEntity(email) {
      case Some(user) => user.passwordShaBase64 = user.sha1Base64(password)
      case None => withPersistenceManager(_.makePersistent(CGUser(email, password)))
    }

  def checkPassword(email: String, password: String): Boolean = 
    withEntity(email) {
      case Some(user) => user.passwordShaBase64 == user.sha1Base64(password)
      case None => false
    }

  def allUsers: Seq[CGUser] =
    withPersistenceManager(pm => 
      pm.newQuery("select from " + classOf[CGUser].getName + " order by email")
      .execute
      .asInstanceOf[java.util.List[CGUser]]
      .map(pm.detachCopy(_)))

  def delete(email: String) {
    withPersistenceManager(pm => pm.deletePersistent(getEntity(pm, email)))
  }

}

@PersistenceCapable(identityType = IdentityType.APPLICATION)
class CGUser {
  @PrimaryKey var email: String = _
  @Persistent var passwordShaBase64: String = _
  @Persistent var salt: String = _

  def sha1Base64(password: String) = CGUser.sha1Base64(salt + password)
}

object CGUser {
  def apply(email: String, password: String): CGUser = {
    val user = new CGUser
    user.email = email
    user.salt = salt
    user.passwordShaBase64 = user.sha1Base64(password)
    user
  }

  private def salt = {
    val random = new SecureRandom
    val bytes = new Array[Byte](20)
    random.nextBytes(bytes)
    base64String(bytes)
  }

  private def base64String(bytes: Array[Byte]) = 
    new String(base64(bytes), "ascii")
  
  private def sha1Base64(text: String) = {
    val md = MessageDigest.getInstance("SHA");
    base64String(md.digest(text.getBytes("utf8")))
  }
}
