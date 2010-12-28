package com.appspot.mondayflicks

import org.scalatest.matchers.ShouldMatchers
import org.scalatra.test.scalatest.ScalatraFunSuite

class SimpleMondayFlicksScalatraFilterSuite extends ScalatraFunSuite with ShouldMatchers {
  addFilter(classOf[MondayFlicksScalatraFilter], "/*")

  test("redirect to start page") {
    get("/") {
      status should be === (302)
      header("Location") should endWith ("/flicks")
    }
  }
}
