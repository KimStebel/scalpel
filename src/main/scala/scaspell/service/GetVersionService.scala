package scaspell.service

import scaspell.api.Spellchecker
import com.twitter.finagle.Service
import com.twitter.finagle.httpx.{Response, Request}

case class GetVersionService(spellchecker: Spellchecker) extends Service[Request, Response] {

     import spray.json._
     import DefaultJsonProtocol._

     override def apply(req: Request) = spellchecker.version() map {
         version =>
             val content: Map[String, String] = Map("version" -> version)

             val res = Response()
             res.setContentString(content.toJson.compactPrint)
             res.setContentType("application/json")
             res.setStatusCode(200)

             res
     }
 }
