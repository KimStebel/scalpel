package scaspell.service

import com.twitter.finagle.Service
import com.twitter.finagle.httpx.{Response, Request}
import com.twitter.util.Future

case class BadRequestService() extends Service[Request, Response] {

    def apply(request: Request): Future[Response] = Future {
        val res = Response()
        res.setStatusCode(402)

        res
    }
    
}
