package uk.co.farwell.scaladays.selenium

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.io.IO
import spray.can.Http

object Boot extends App with Logger {
  val interface = "localhost"
  val port = 9100
  val config = loadConfig()

  logger.info("binding to interface " + interface + " port " + port)

  implicit val system = ActorSystem("on-spray-can", config)
  val service = system.actorOf(Props[MyServiceActor], "my-service")

  IO(Http) ! Http.Bind(service, interface = interface, port = port)

  def loadConfig(): Config = ConfigFactory.systemProperties().withFallback(ConfigFactory.load())
}