package com.appspot.mondayflicks

import javax.jdo.annotations._
import com.google.appengine.api.memcache.{MemcacheService, MemcacheServiceFactory}

object KeyValueStore extends PersistenceManagerSupport[KeyValuePair] with util.Logging {

  private lazy val cache: MemcacheService = 
    MemcacheServiceFactory.getMemcacheService

  def readOrElse(key: String, default: String): String =
    Option(cache.get(key)) match {
      case Some(value: String) => value
      case None =>
        debug("Cache miss, looking up " + key + " in the datastore.")
        withEntity(key) {
          case Some(pair) => 
            cache.put(key, pair.value)
            pair.value
          case None => default
        }
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
    cache.put(key, value)
  }
}

@PersistenceCapable(identityType = IdentityType.APPLICATION)
class KeyValuePair {
  @PrimaryKey var key: String = _
  @Persistent var value: String = _
}
