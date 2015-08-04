package scaspell.util

import org.jboss.netty.handler.codec.http.QueryStringDecoder

case class Params(private val uri: String) {
    private lazy val params = new QueryStringDecoder(uri).getParameters

    def getInt(key: String): Option[Int] = params.containsKey(key) match {
        case true =>
            Some(Integer.parseInt(params.get(key).get(0)))
        case _ => None
    }

    def get(key: String): Option[String] = params.containsKey(key) match {
        case true =>
            Some(params.get(key).get(0))
        case _ => None
    }

    def getSeq(key: String): Option[Seq[String]] = params.containsKey(key) match {
        case true =>
            import scala.collection.JavaConversions._
            Some(params.get(key).toIndexedSeq)
        case _ => None
    }
}
