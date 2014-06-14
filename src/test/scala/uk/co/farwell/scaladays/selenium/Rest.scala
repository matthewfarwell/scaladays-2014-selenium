package uk.co.farwell.scaladays.selenium

import java.nio.charset.Charset
import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.apache.http.impl.client.HttpClientBuilder

object Services {
  val baseUrl = "http://localhost:9100"
  def client(service: String) = new RestClient(baseUrl, service)

  def listUsers(): JsonResponse[List[UsersRow]] = client("/users/list").get()
}

case class JsonResponse[T](statusCode: Int, headers: Map[String, String], body: Option[String]) {
  def unmarshall()(implicit m: Manifest[T]): T = {
    body match {
      case Some(x) if (x.length > 0) => JsonUtil.fromJson(x).asInstanceOf[T]
      case _ => throw new RuntimeException("no body to unmarshall statusCode=" + statusCode + " body=" + body)
    }
  }
}

class RestClient(prefix: String, service: String) {
  private val logger = LoggerFactory.getLogger(this.getClass())

  def get[T](): JsonResponse[T] = send[T, JsonResponse[T]]({ JsonResponse.apply _ })

  private def send[T, U](transform: (Int, Map[String, String], Option[String]) => U): U = {
    val request = new HttpGet(prefix + service)

    try {
      val httpclient = HttpClientBuilder.create().build()

      val response = httpclient.execute(request)
      val entity = response.getEntity()

      val statusCode = response.getStatusLine().getStatusCode()
      val responseHeaders = toHeaderMap(response.getAllHeaders())
      val responseBody = toString(entity)

      transform(statusCode, responseHeaders, responseBody)
    } catch {
      case e: Exception => throw new RuntimeException(e);
    } finally {
      request.releaseConnection();
    }
  }

  private def toString(entity: HttpEntity) = Option(entity).flatMap(e => Some(EntityUtils.toString(entity, Charset.forName("UTF-8"))))
  private def toHeaderMap(headers: Array[Header]): Map[String, String] = headers.map(h => (h.getName(), h.getValue())).toMap
}

object JsonUtil {
  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  def fromJson[T](json: String)(implicit m: Manifest[T]): T =  mapper.readValue[T](json)
}
