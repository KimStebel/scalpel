package scaspell.api

import org.scalatest._
import com.twitter.util.Duration
import java.util.concurrent.TimeUnit
import com.twitter.util.Await
import com.twitter.util.Future

class SpellcheckerSpec extends FlatSpec with Matchers {

  lazy val aspell:Spellchecker = Aspell()
  
  def await[A](f:Future[A]) = Await.result(f, Duration(10, TimeUnit.SECONDS))
  
  "The Aspell spellchecker" should "find a spelling error in 'tesd'" in {
    val errors = await(aspell.check("tesd", "email", "en", None)).keys
    errors should contain("tesd")
  }
  
  val sentence = "this is just a simple test sentence without any fancy words"
  it should s"not find a spelling error in '$sentence'" in {
    val errors = await(aspell.check(sentence, "email", "en", None)).keys
    errors shouldBe empty
  }

  it should "list 'en' as one of the available languages" in {
    val al = await(aspell.availableLanguages(None))
    al should contain("en")
  }
  
  it should "list 'email' as one of the available modes" in {
    val modes = await(aspell.availableModes()).keys
    modes should contain("email")
  }
  
  it should "report a non-empty version string" in {
    val r = await(aspell.version())
    r should not be empty
  }
  
  it should "be able to use a custom dictionary" in {
    val errors = await(aspell.check("CouchDB is cool.", "email", "en", customDictionary = Seq("CouchDB")))
    errors shouldBe empty
    val errors2 = await(aspell.check("Abcdef.", "email", "en", customDictionary = Seq("Abcdef")))
    errors2 shouldBe empty
    val errors3 = await(aspell.check("Cloudant isn't CouchDB.", "email", "en", customDictionary = Seq("CouchDB Cloudant")))
    errors3 shouldBe empty
    
  }
}