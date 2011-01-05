package com.appspot.mondayflicks

import javax.servlet.FilterConfig
import org.scalatra._

trait TweeterSupport extends Initializable {

  override type Config = FilterConfig

  protected var tweeter: Tweeter = _

  override abstract def initialize(config: FilterConfig): Unit = {
    super.initialize(config)
    val context = config.getServletContext
    tweeter = new Tweeter(context getInitParameter "twitter-consumer-key",
                          context getInitParameter "twitter-consumer-secret",
                          context getInitParameter "twitter-token",
                          context getInitParameter "twitter-token-secret")
  }
}
