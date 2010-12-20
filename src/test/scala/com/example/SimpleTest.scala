import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite

class SimpleTest extends FunSuite with ShouldMatchers {
  test("are we up and running") {
    2 + 3 should equal (5)
  }
}
