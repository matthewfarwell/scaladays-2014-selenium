package uk.co.farwell.scaladays.selenium

import java.io.PrintWriter
import org.junit.runner.RunWith
import org.openqa.selenium.WebDriver
import org.scalatest.Args
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.Reporter
import org.scalatest.Status
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.IntegrationPatience
import org.scalatest.events.Event
import org.scalatest.events.TestFailed
import org.scalatest.selenium.Chrome
import org.scalatest.selenium.Driver
import org.scalatest.selenium.Firefox
import org.scalatest.selenium.WebBrowser
import org.scalatest.junit.JUnitRunner

class UserListSpec5 extends CommonSelenium with Firefox {

  "list" should "default to 10 items" in {
    go to (baseUrl + "admin/index.html#/users")

    eventually {
      val tds = findAll(xpath("//tr//td[starts-with(@id, 'userUsername_')]")).map(_.text).toList

      println("list=" + tds)
      tds.size should be(10)
    }
  }

}
