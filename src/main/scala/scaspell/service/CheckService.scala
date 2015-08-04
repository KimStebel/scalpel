package scaspell.service

import scaspell.api.Spellchecker
import com.twitter.finagle.Service
import com.twitter.finagle.httpx.{Response, Request}

case class CheckService(spellchecker: Spellchecker) extends Service[Request, Response] {

     import spray.json._
     import DefaultJsonProtocol._

     override def apply(req: Request) = {
         spellchecker.check(req.contentString,
             req.params.get("mode"),
             req.params.get("lang"),
             req.params.getInt("limit")) map {
             result =>
                 val res = Response()
                 res.setContentString(result.toJson.compactPrint)
                 res.setContentType("application/json")
                 res.setStatusCode(200)

                 res
         }
     }
 }
