package com.appspot.mondayflicks

import javax.servlet.FilterConfig
import org.scalatra._

trait TweeterSupport extends Initializable with util.Logging {

  override type Config = FilterConfig

  protected var tweeter: Tweeter = _

  override abstract def initialize(config: FilterConfig): Unit = {
    super.initialize(config)
    val context = config.getServletContext
    val twitterConsumerKey = context getInitParameter "twitter-consumer-key"
    tweeter = if (twitterConsumerKey != null)
      new TwitterTweeter(twitterConsumerKey,
                         context getInitParameter "twitter-consumer-secret",
                         context getInitParameter "twitter-token",
                         context getInitParameter "twitter-token-secret")
      else {
        warn("No twitter-consumer-key: configuring dummy implementation")
        new DummyTweeter
      }
  }
}
