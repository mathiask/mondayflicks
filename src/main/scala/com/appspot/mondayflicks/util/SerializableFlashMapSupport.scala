package com.appspot.mondayflicks.util

import org.scalatra._

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import scala.util.DynamicVariable

object SerializableFlashMapSupport {
  val sessionKey = SerializableFlashMapSupport.getClass.getName+".key"
}

/** Taken from scalatra. */
trait SerializableFlashMapSupport extends Handler {
  import SerializableFlashMapSupport.sessionKey

  abstract override def handle(req: HttpServletRequest, res: HttpServletResponse) {
    _flash.withValue(getFlash(req)) {
      super.handle(req, res)
      flash.sweep()
      req.getSession.setAttribute(sessionKey, flash)
    }
  }

  private def getFlash(req: HttpServletRequest) =
    req.getSession.getAttribute(sessionKey) match {
      case flashMap: SerializableFlashMap => flashMap
      case _ => SerializableFlashMap()
    }


  private val _flash = new DynamicVariable[SerializableFlashMap](null)

  protected def flash = _flash.value
}

@serializable class SerializableFlashMap extends FlashMap

object SerializableFlashMap {
  def apply() = new SerializableFlashMap
}
