package scaspell.markdown

import org.scalatest._
import org.pegdown.ast._
import scala.collection.JavaConversions._
import org.pegdown.ast

class ParserSpec extends FlatSpec with Matchers {
    
  "The Parser" should "convert paragraphs to dita" in {
    val res = Parser.mdToDita(
"""this is the first paragraph
still the 1st

and this is the second  
""")
    res should equal(
"""<p>this is the first paragraph
still the 1st</p>

<p>and this is the second
</p>""")
  }
  
  it should "convert ``` to <codeblock>" in {
    val res = Parser.mdToDita(
"""```shell
this is some code
  and some indented code
and more code
```
""")
    res should equal(
"""<codeblock>
this is some code
  and some indented code
and more code
</codeblock>
""")
  }
  
  it should "convert ` to <codeph>" in {
    Parser.mdToDita("this is some text with `code` in it\n") should equal("<p>this is some text with <codeph>code</codeph> in it</p>")
    
  }
  
  it should "convert headers to topic with title" in {
    Parser.mdToDita("#header\n\n") should equal("<section><title>header</title></section>\n\n")
    
  }
  
  it should "convert links to xrefs" in {
    Parser.mdToDita("some text with a [link](https://example.com) in it\n") should
      equal("""<p>some text with a <xref href="https://example.com">link</xref> in it</p>""")
  }
  
  it should "convert block quotes to section titles" in {
    Parser.mdToDita("> a blockquote") should equal("""<section><title>a blockquote</title></section>

""")
  
  }
  
  it should "convert * to <i>" in {
    Parser.mdToDita("a text with *emphasis* and stuff\n") should equal("<p>a text with <i>emphasis</i> and stuff</p>")
  }
  
  it should "convert ** to <b>" in {
    Parser.mdToDita("a text with **strong emphasis** and stuff\n") should equal("<p>a text with <b>strong emphasis</b> and stuff</p>")
  }
  
  it should "convert unordered lists" in {
    Parser.mdToDita(" * first\n * second\n\n") should equal("<ul><li>first</li>\n<li>second</li></ul>\n")
  }
  
  it should "convert ordered lists" in {
    Parser.mdToDita("1. first\n2. second\n\n") should equal("<ol><li>first</li>\n<li>second</li></ol>\n")
  }
  
  it should "remove html tags" in {
    Parser.mdToDita("""<div>
something
</div>
""") should equal("""
something

""")
    
  }
  
  /* it should "convert tables" in {
    val table =
"""
| h1 | h2 |
| r1c1 | r1c2 |
| r2c2 | r2c2 |
"""
    Parser.mdToDita(table) should equal(
"""<simpletable>
<sthead>h1<stentry>h2</stentry></sthead>
<strow><stentry>r1c1</stentry><stentry>r1c2</stentry></strow>
<strow><stentry>r2c1</stentry><stentry>r2c2</stentry></strow>
</simpletable>""")
  } */
  
  it should "produce the right number of lines for a longer document" in {
    val md = 
"""# Header

This is some text.
And here is some more text in the same paragraph.

This is the second paragraph. It has just one line.

## Level 2 Header

 * First item
 * Second item

1. First ordered item
2. Second ordered item

```json
{ "json": true }
```

> A block quote

And finally we have some *text* with **strong emphasis** and inline `code`.
"""
    val res = scaspell.markdown.Parser.mdToDita(md)
    println(res)
    res.split("\n").size should equal(md.split("\n").size)
    
  }
  
  
  
}
