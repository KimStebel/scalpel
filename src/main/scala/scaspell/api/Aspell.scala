package scaspell.api

import com.twitter.util.{Await, Future}
import com.typesafe.scalalogging.LazyLogging

object Aspell {

}

case class Aspell() extends Spellchecker with LazyLogging {

    import scala.sys.process._

    private lazy val _availableLanguages: Seq[String] = {
        Await.result(availableLanguages())
    }

    private lazy val _availableModes: Seq[String] = {
        Await.result(availableModes()).map(_._1).toSeq
    }

    def availableLanguages(filter: Option[String] = None): Future[Seq[String]] = Future {
        val languages = Process("aspell dump dicts").lineStream.map(_.trim)

        filter.map {
            case "main" =>
                languages.filter(!_.contains("-"))
            case _ => languages
        }.getOrElse(languages)
    }

    def version(): Future[String] = Future {
        "aspell -v".!!.replace("@(#) ", "").trim
    }

    def checkWords(words: Seq[String], lang: Option[String] = None, limit: Option[Int] = None): Future[Map[String, Seq[String]]] = Future {
        exec(lang.getOrElse("en"), limit) {
            words.mkString(" ")
        }
    }

    private lazy val resultRegEx = """&\s([^\s]*)\s(\d*)\s(\d*):(.*)""".r

    private def parse(result: String, limit: Int): Option[(String, Seq[String])] = {
        resultRegEx.findAllIn(result).matchData.map(m => {
            (m.group(1), m.group(4).split(',').map(_.trim).take(limit).toSeq)
        }).toSeq.headOption
    }

    def checkHtml(html: String, lang: Option[String] = None, limit: Option[Int] = None): Future[Map[String, Seq[String]]] = Future {
        exec(lang.getOrElse("en"), limit) {
            Strings.toHtml(html)
        }
    }

    private def exec(lang: String = "en",
                     limit: Option[Int] = None,
                     mode:String = "email")(f: => String): Map[String, Seq[String]] = {
        val l = Strings.toLanguageParameter(lang)
        val m = Strings.toModeParameter(mode)

        val echo = s"echo $f"
        val aspell = s"aspell pipe --encoding=utf-8 $l $m"
        val grep = """grep -v "\*|\@|^$""""
        
        logger.debug(s"calling aspell: $echo | $aspell | $grep | uniq")
        val results = (echo #| aspell #| grep #| "uniq").lineStream_!

        results.filter(r => !r.isEmpty && r(0) == '&')
        results.map(parse(_, limit.getOrElse(25))).filter(_.isDefined).map(_.get).toMap
    }

    /**
     * Lists available modes
     *
     * @return
     */
    override def availableModes() = Future {
        "aspell --dump=modes".lineStream_!.map {
            line =>
                val s = line.split(" ", 2)

                (s(0).trim, s(1).trim)
        }.toMap
    }

    /**
     * Produce a list of misspelled words
     *
     * @param input
     * @param mode
     * @param lang
     * @param limit
     * @return
     */
    override def check(input: String, mode: String, lang: String, limit: Option[Int]) = Future {
        exec(lang, limit, mode) { Strings.toHtml(input) }
    }
}



object Strings {

    def toHtml(html: String): String = {
        html.replaceAll("\\|", " ")
    }

    def toLanguageParameter(lang: String): String = "--lang=" + lang
    

    def toModeParameter(mode: String): String = "--mode=" + mode

}