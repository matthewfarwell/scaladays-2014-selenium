package uk.co.farwell.scaladays.selenium

import scala.collection.JavaConversions._
import org.scalatest.concurrent.Eventually
import org.scalatest.selenium.WebBrowser
import org.scalatest.selenium.Firefox
import org.scalatest.Matchers
import org.scalatest.concurrent.IntegrationPatience

class AmIFamousScala extends Eventually with WebBrowser with Firefox with Matchers with IntegrationPatience {

  def fn(): Unit = {
    try {
      go to "http://www.google.ch/"

      textField("q").value = "Farwell"
      click on "btnG"

      eventually {
        find(tagName("body")).get.text should include("farwell.co.uk")
      }

      println("Yay, you are famous")
    } finally {
      webDriver.quit()
    }
  }
}

object AmIFamousScala extends App {
  new AmIFamousScala().fn()
}
