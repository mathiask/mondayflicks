package com.appspot.mondayflicks.util

import java.util.{Date, Calendar, GregorianCalendar, TimeZone}
import Calendar._

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.WordSpec

class DateOnlySpec extends WordSpec with ShouldMatchers {

  import DateOnly._
  
  private def christmas2010 = date(2010, 12, 25)

  private def date(year: Int, month: Int, day: Int): Date = {
    val cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"))
    cal.set(YEAR, year)
    cal.set(MONTH, month - 1)
    cal.set(DAY_OF_MONTH, day)
    cal.getTime
  }

  "A DataOnlySpec's string representation" should {
    "be in ISO 8601 format" in {
      (new DateOnly(christmas2010)).toString should be === "2010-12-25"
    }
  }

  def given = afterWord("given")

  "A DataOnlySpec" can {
    "be constructed" when given {
      "a string" in {
        DateOnly("2001-09-30").toString should be === "2001-09-30"
      }

      "a date" in {
        DateOnly(christmas2010).toString should be === "2010-12-25"
      }

      "nothing for today" in {
        DateOnly.dateFactory = () => date(2010, 12, 28)
        DateOnly.today.toString should be === "2010-12-28"
      }
    }
  }

  "isBefore" should {
    "be before in the previous year" in {
      DateOnly("2009-01-20") isBefore DateOnly("2010-12-20") should be (true)
    }

    "be before in the previous year even when the rest is later" in {
      DateOnly("2009-10-20") isBefore DateOnly("2010-01-02") should be (true)
    }

    "be before in the same year" in {
      DateOnly("2010-05-20") isBefore DateOnly("2010-06-01") should be (true)
    }

    "be before in the same year and month" in {
      DateOnly("2010-05-20") isBefore DateOnly("2010-05-21") should be (true)
    }

    "not be before on the same day" in {
      DateOnly("2010-05-20") isBefore DateOnly("2010-05-20") should be (false)
    }

    "not be before when later" in {
      DateOnly("2011-12-31") isBefore DateOnly("2010-05-20") should be (false)
    }

    "work for implicit right hand side" in {
      DateOnly("2010-10-31") isBefore date(2010, 11, 1) should be (true)
    }

    "work for implicit left hand side" in {
       date(2010, 11, 1) isBefore DateOnly("2011-10-31") should be (true)
    }

    "work for two implicit arguments" in {
      date(2005, 1, 1) isBefore date(2010, 11, 1) should be (true)
    }
  }

  "isBeforeToday" should {
    "work" in {
      DateOnly.dateFactory = () => date(2010, 12, 28)
      DateOnly("2010-10-10").isBeforeToday should be (true)
    }
  }

  "Implcits" should {
    "convert to DateOnly" in {
      val d: DateOnly = date(2010, 1, 2)
      d.getClass should be === classOf[DateOnly]
    }

    "convert to Date" in {
      val d = DateOnly("2010-12-14")
      val d2: Date = d;
      d2.getTime should not be === (0)
    }
  }

}
