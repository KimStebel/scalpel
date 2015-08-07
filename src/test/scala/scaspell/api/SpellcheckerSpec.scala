package scaspell.api

import org.scalatest._
import com.twitter.util.Duration
import java.util.concurrent.TimeUnit
import com.twitter.util.Await
import com.twitter.util.Future

class SpellcheckerSpec extends FlatSpec with Matchers {

  def await[A](f:Future[A]) = Await.result(f, Duration(10, TimeUnit.SECONDS))
  
  "The Aspell spellchecker" should "find a spelling error in 'tesd'" in {
    val aspell:Spellchecker = Aspell()
    val errors = await(aspell.check("tesd", "email", "en", None)).keys
    errors should contain("tesd")
  }

  it should "TODO" in {
    
  }
}