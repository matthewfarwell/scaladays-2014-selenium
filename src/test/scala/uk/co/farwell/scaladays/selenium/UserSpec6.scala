package uk.co.farwell.scaladays.selenium

import org.junit.runner.RunWith
import org.scalatest.selenium.Chrome
import org.scalatest.selenium.Firefox
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class UserSpecFirefox extends UserSpecBase with Firefox

class UserSpecChrome extends UserSpecBase with Chrome

abstract class UserSpecBase extends CommonSelenium {
  it should "redirect to admin index.html - eventually" in {
    go to baseUrl

    eventually {
      currentUrl should be(baseUrl + "admin/index.html#/users")

      find("pageTitle").get.text should be("Users")

      textField("userSearch").value should be('empty)

      find("userUsername_2").get.text should be("matthew")
    }
  }


}
