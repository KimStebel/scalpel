package scaspell.util

import com.twitter.finagle.httpx.{Response, Request}
import com.twitter.finagle.Service
import com.twitter.finagle.httpx.service.RoutingService

object RequestAwareRoutingService {
    def byRequest[REQUEST <: Request](routes: PartialFunction[Request, Service[REQUEST, Response]]) =
        new RoutingService(
            new PartialFunction[Request, Service[REQUEST, Response]] {
                def apply(request: Request) = routes(request)

                def isDefinedAt(request: Request) = routes.isDefinedAt(request)
            })
}
