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

class UserListSpec3 extends CommonSelenium with Firefox {
  "user" should "be able to edit another user" in {
    go to (baseUrl + "admin/index.html#/users")

    eventually {
      click on "userEdit_2"
    }

    eventually {
      currentUrl should be(baseUrl + "admin/index.html#/users/2/edit")

      find("pageTitle").get.text should be("Edit user")

      textField("userUsername").value should be("matthew")
      textField("userUsername").isEnabled should be(false)

      textField("userFullname").value should be("Matthew Farwell")
      textField("userFullname").isEnabled should be(true)
    }

    textField("userFullname").value = "Matthew Farwell is great"
    pwdField("userPassword").value = "hello world"
    pwdField("userConfirmPassword").value = "hello world"

    find("errorUserConfirmPassword").get.text should be("Passwords do not match")
  }
}
