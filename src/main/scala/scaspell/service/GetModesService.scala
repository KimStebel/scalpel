package scaspell.service

import scaspell.api.Spellchecker
import com.twitter.finagle.Service
import com.twitter.finagle.httpx.{Response, Request}
import com.twitter.util.Future

case class GetModesService(spellchecker:Spellchecker) extends Service[Request, Response] {
    import spray.json._
    import DefaultJsonProtocol._

    def apply(request: Request): Future[Response] = spellchecker.availableModes() map {
        modes =>
            val res = Response()
            res.setContentString(modes.toJson.compactPrint)
            res.setContentType("application/json")
            res.setStatusCode(200)
            res
    }
}
