package scaspell

import com.twitter.finagle.httpx.{Http, Request, Method}
import com.twitter.finagle.Httpx
import java.net.InetSocketAddress
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.httpx.service.RoutingService
import com.twitter.finagle.httpx.path._
import scaspell.api.Aspell
import scaspell.util.RequestAwareRoutingService
import scaspell.service._
import com.twitter.finagle.httpx.path./

object Server extends App {
    private lazy val root = Root
    private lazy val spellchecker = Aspell()
    private lazy val getMethodService = GetModesService(spellchecker)
    private lazy val getLanguageService = GetLanguagesService(spellchecker)
    private lazy val getVersionService = GetVersionService(spellchecker)
    private lazy val checkService = CheckService(spellchecker)
    private lazy val badRequest = BadRequestService()

    val routing: RoutingService[Request] = RequestAwareRoutingService.byRequest[Request] {
        case r: Request => (r.method, Path(r.path)) match {
            case Method.Get -> `root` / "mode" => getMethodService
            case Method.Get -> `root` / "language" => getLanguageService
            case Method.Get -> `root` / "version" => getVersionService
            case Method.Post -> `root` / "check" => checkService
            case Method.Get -> `root` / "check" => checkService
            case _ => badRequest
        }
        case _ => badRequest
    }
    
    val httpServer = Httpx.serve(":8080", routing)
    
}




