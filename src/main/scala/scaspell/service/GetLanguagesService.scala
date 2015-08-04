package scaspell.service

import scaspell.api.Spellchecker
import com.twitter.finagle.Service
import com.twitter.finagle.httpx.{Response, Request}

case class GetLanguagesService(spellchecker: Spellchecker) extends Service[Request, Response] {

    import spray.json._
    import DefaultJsonProtocol._

    override def apply(req: Request) = spellchecker.availableLanguages(req.params.get("filter")) map {
        modes =>
            val res = Response()
            res.setContentString(modes.toJson.compactPrint)
            res.setContentType("application/json")
            res.setStatusCode(200)

            res
    }
}
