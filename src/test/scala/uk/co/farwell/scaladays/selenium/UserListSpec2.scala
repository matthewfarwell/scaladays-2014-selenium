package uk.co.farwell.scaladays.selenium

import org.scalatest.selenium.Firefox

class UserListSpec2 extends CommonSelenium with Firefox {
  import Services._

  "slash" should "redirect to admin index.html - eventually" in {
    go to baseUrl

    eventually {
      currentUrl should be(baseUrl + "admin/index.html#/users")

      find("pageTitle").get.text should be("Users")

      textField("userSearch").value should be('empty)

      find("userUsername_2").get.text should be("matthew")
    }
  }
}
