package scaspell

import com.twitter.finagle.Service
import com.twitter.finagle.httpx.{Response, Request}
import scaspell.api.Aspell
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.HttpMethod
import spray.json._
import spray.json.DefaultJsonProtocol._
import scala.collection.JavaConversions.asScalaBuffer

case class AspellService() extends Service[Request, Response] {

    import scala.collection.JavaConversions._

    private lazy val aspell = Aspell()

    def apply(req: Request): Future[Response] = {
        val res = Response()
        res.setStatusCode(404)

        req.method match {
            case HttpMethod.GET => req.uri match {
                case uri: String if uri.startsWith("/words") =>
                    req.getParams("q").toSeq match {
                        case w: Seq[String] => aspell.checkWords(w, Option(req.getParam("lang")), Option(req.getIntParam("limit"))) map {
                            words =>
                                val content = words.toJson.compactPrint
                                res.setContentString(content)
                                res.setContentType("application/json")
                                res.setStatusCode(200)

                                res
                        }
                        case _ => Future.value(res)
                    }
                case uri: String if uri.startsWith("/html") =>
                    Option(req.getParam("q")) match {
                        case Some(html) => aspell.checkHtml(html, Option(req.getParam("lang")), Option(req.getIntParam("limit"))) map {
                            words =>
                                val content = words.toJson.compactPrint
                                res.setContentString(content)
                                res.setContentType("application/json")
                                res.setStatusCode(200)

                                res
                        }
                        case _ => Future.value(res)
                    }
                case _ => Future.value(res)
            }
            case _ => Future.value(res)
        }
    }
}
