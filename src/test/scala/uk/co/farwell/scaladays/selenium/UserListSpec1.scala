package uk.co.farwell.scaladays.selenium

import org.scalatest.selenium.Firefox

class UserListSpec1 extends CommonSelenium with Firefox {

  "slash" should "redirect to admin index.html" in {
    go to baseUrl

    currentUrl should be(baseUrl + "admin/index.html#/users")

    find("pageTitle").get.text should be("Users")

    textField("userSearch").value should be('empty)

    find("userUsername_2").get.text should be("matthew")
  }
}
