package scaspell.service

import scaspell.api.Spellchecker
import com.twitter.finagle.Service
import com.twitter.finagle.httpx.{Response, Request}
import scaspell.util.FutureUtils._
import com.twitter.util.Future

case class CheckService(spellchecker: Spellchecker) extends Service[Request, Response] {

   import spray.json._
   import DefaultJsonProtocol._

   override def apply(req: Request) = {
     val mode = req.params.get("mode").getOrElse("email")
     val lang = req.params.get("lang").getOrElse("en")
     for {
       availableModes <- spellchecker.availableModes
       availableLanguages <- spellchecker.availableLanguages()
       res <- 
       (if (availableModes.contains(mode) && availableLanguages.contains(lang)) {
         spellchecker.check(
           req.contentString,
           mode,
           lang,
           req.params.getInt("limit")
         ) map { result =>
           val res = Response()
           res.setContentString(result.toJson.compactPrint)
           res.setContentType("application/json")
           res.setStatusCode(200)
           res
         }
       } else {
         val res = Response()
         res.setStatusCode(400)
         Future(res)
         
       })
     } yield {
       res
     }
   }
 }
