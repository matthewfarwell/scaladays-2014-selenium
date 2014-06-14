package uk.co.farwell.scaladays.selenium

import akka.actor.Actor
import spray.http._
import spray.http.HttpHeaders._
import spray.http.HttpMethods._
import spray.http.MediaTypes._
import spray.http.MediaTypes._
import spray.httpx._
import spray.httpx.RequestBuilding._
import spray.httpx.marshalling._
import spray.httpx.unmarshalling._
import spray.json._
import spray.json.DefaultJsonProtocol._
import spray.routing._
import org.slf4j.LoggerFactory
import scala.util.Random
import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global

case class Ping(status: String)

class MyServiceActor extends Actor with MyService {
  def actorRefFactory = context

  def receive = runRoute(myRoute)
}

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val pingFormat = jsonFormat1(Ping)
  implicit val usersRowFormat = jsonFormat5(UsersRow)
}

object MyMarshallers {
  import MyJsonProtocol._

  val emptyUsersRow = UsersRow(0, None, None, None, None)

  def unmarshallFromJson[T](empty: T)(implicit ev: JsonFormat[T]) = Unmarshaller[T](`application/json`) {
    case HttpEntity.NonEmpty(contentType, data) => {
      val json = JsonParser(data.asString(charset = HttpCharsets.`UTF-8`))
      jsonReader[T].read(json)
    }

    case HttpEntity.Empty => empty
  }

  implicit val usersRowUnmarshaller = unmarshallFromJson[UsersRow](emptyUsersRow)

  private def marshallToJson[T]()(implicit ev: JsonFormat[T]) = Marshaller.delegate[T, String](`application/json`)((xe) => xe.toJson.toString)

  implicit val pingMarshaller: Marshaller[Ping] = marshallToJson[Ping]()

  implicit val jsonUsersRowMarshaller: Marshaller[UsersRow] = marshallToJson[UsersRow]()
  implicit val jsonUsersListMarshaller: Marshaller[List[UsersRow]] = marshallToJson[List[UsersRow]]()
}

trait MyService extends HttpService with Logger {
  import MyMarshallers._
  val dao: UserDao = new UserDao
  val validator = new UserValidator(dao)
  val slow = true

  val myRoute =
    path("ping") {
      get {
        respondWithMediaType(`application/json`) {
          complete {
            Ping("OK")
          }
        }
      }
    } ~ pathPrefix("admin") {
      getFromResourceDirectory("webapp")
    } ~ pathPrefix("users") {
      path("list") {
        get {
          respondWithMediaType(`application/json`) {
            complete {
              sleep(2000, 3000)
              dao.list()
            }
          }
        }
      } ~
        path("create") {
          post {
            respondWithMediaType(`application/json`) {
              entity(as[UsersRow]) { sr =>
                val validation = validator.validateCreate(sr)

                validate(validation.condition, validation.message) {
                  complete {
                    sleep(2000, 3000)
                    dao.create(sr)
                  }
                }
              }
            }
          }
        } ~
        path(IntNumber) { id =>
          get {
            respondWithMediaType(`application/json`) {
              complete {
                sleep(2000, 3000)
                dao.get(id)
              }
            }
          } ~ put {
            respondWithMediaType(`application/json`) {
              entity(as[UsersRow]) { tr =>
                val validation = validator.validateUpdate(id, tr)

                validate(validation.condition, validation.message) {
                  complete {
                    sleep(2000, 3000)
                    dao.update(id, tr)
                  }
                }
              }
            }
          }
        }
    } ~
      pathEndOrSingleSlash {
        get {
          redirect("/admin/index.html", StatusCodes.TemporaryRedirect)
        }
      }

  private def sleep(min: Int, max: Int) = if (slow) Thread.sleep(Random.nextInt(max - min) + min)
}

trait Logger {
  val logger = LoggerFactory.getLogger(this.getClass());
}