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

class UserListSpec4 extends CommonSelenium with Firefox {
  "user" should "be able to add another user" in {
    go to (baseUrl + "admin/index.html#/users")

    val result1 = executeScript("return document.title;")
    result1 should be("My application")

    eventually {
      click on "newUser"
    }

    eventually {
      currentUrl should be(baseUrl + "admin/index.html#/users/new")

      find("pageTitle").get.text should be("New user")
    }

    textField("userUsername").value = "derek"
    textField("userFullname").value = "Derek and the Dominoes"
    pwdField("userPassword").value = "derek"
    pwdField("userConfirmPassword").value = "derek"
    singleSel("userRole").value = "1" // TODO do something about this

    val listBefore = Services.listUsers().unmarshall

    println("listBefore=" + listBefore)

    click on "submit"

    eventually {
      val listAfter = Services.listUsers().unmarshall

      println("listAfter =" + listAfter)

      listAfter.size should be(listBefore.size + 1)

      val u = listAfter.find(u => u.username == Some("derek")).get

      u.fullName should be(Some("Derek and the Dominoes"))
      u.id should not be (None)
    }

  }
}
