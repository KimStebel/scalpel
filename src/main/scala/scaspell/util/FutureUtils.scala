package scaspell.util

import com.twitter.util.Future
import com.twitter.util.Await

object FutureUtils {
  implicit def with_![A](f:Future[A]) = new {
    def ! = Await.result(f)
  }
}