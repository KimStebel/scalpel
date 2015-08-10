package scaspell

import org.scalatest._
import com.twitter.util.Duration
import java.util.concurrent.TimeUnit
import com.twitter.util.{Await,Future}
import com.twitter.finagle.Service
import com.twitter.util.Closable
import scaspell.util.FutureUtils._
import com.twitter.finagle.httpx.Http
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.httpx.Request
import com.twitter.finagle.httpx.Response
import com.twitter.finagle.ListeningServer
import org.jboss.netty.handler.codec.http.{DefaultHttpRequest, HttpRequest, HttpResponse, HttpVersion, HttpMethod}
import com.twitter.finagle.httpx.Status
import com.twitter.finagle.httpx.Method
import com.twitter.finagle.Httpx

class ServerSpec extends FlatSpec with Matchers with BeforeAndAfterEach {
  
  var server: ListeningServer = _
  var client: Service[Request, Response] = _
  override def beforeEach() {
    server = Server.httpServer
    client = ClientBuilder()
      .codec(Http())
      .hosts(Seq(server.boundAddress))
      .hostConnectionLimit(1)
      .build()
  }
  override def afterEach() {
    Closable.all(server, client).close().!
  }
  
  "The Server" should "return 404 for a request to /nonexistant" in {
    val request = Request("/nonexistant")
    val responseFuture = client(request)
    val response = Await.result(responseFuture)
    response.status should equal(Status.NotFound)
  }
  
  it should "return 200 and an empty json object for a post request to /spelling/check" in {
    val request = Request(Method.Post, "/spelling/check")
    val response = client(request).!
    response.status should equal(Status.Ok)
    response.getContentString should equal("{}")
  }

}