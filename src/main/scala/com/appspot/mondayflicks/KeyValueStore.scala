package com.appspot.mondayflicks

import javax.jdo.annotations._

object KeyValueStore extends PersistenceManagerSupport[KeyValuePair] {
  def readOrElse(key: String, default: String): String =
    withEntity(key) {
      case Some(pair) => pair.value
      case None => default
    }

  def set(key: String, value: String) {
    withEntity(key) {
      case Some(pair) => pair.value = value
      case None => 
        val kv = new KeyValuePair
        kv.key = key
        kv.value = value
        withPersistenceManager(_.makePersistent(kv))
    }
  }
}

@PersistenceCapable(identityType = IdentityType.APPLICATION)
class KeyValuePair {
  @PrimaryKey var key: String = _
  @Persistent var value: String = _
}
