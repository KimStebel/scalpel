package scaspell.markdown

import org.pegdown.PegDownProcessor
import org.pegdown.ast._
import org.pegdown.ast
import scala.collection.JavaConversions._
import eu.henkelmann.actuarius.XmlChunk


object Parser {
  private  def parseRoot(md:String):RootNode = {
    val p = new PegDownProcessor
    p.parseMarkdown(md.toCharArray)
  }
  
  
  def mdToDita(md:String) = {
    import eu.henkelmann.actuarius.ActuariusTransformer
    import eu.henkelmann.actuarius.{Decorator, Transformer}
    
    val transformer = new Transformer with Decorator {
      private var inBlockQuote = false
      override def deco() = this
      override def allowVerbatimXml = true
      override def decorateParagraphOpen = if (inBlockQuote) "" else "<p>"
      override def decorateParagraphClose = if (inBlockQuote) "" else "</p>"
      override def decorateEmphasis(text:String) = "<i>" + text + "</i>"
      override def decorateBreak() = "\n"
      override def decorateCodeBlockOpen = "<codeblock>\n"
      override def decorateCodeBlockClose = "</codeblock>\n"
      override def decorateCode(code:String) = s"<codeph>${code}</codeph>"
      override def decorateHeaderOpen(headerNo:Int) = "<section><title>"
      override def decorateHeaderClose(headerNo:Int) = "</title></section>\n\n"
      override def decorateLink(text:String, url:String, title:Option[String]) = s"""<xref href="$url">${text}${title.map(t => s" ("+t+")").getOrElse("")}</xref>"""
      override def decorateBlockQuoteOpen() = {
        inBlockQuote = true
        decorateHeaderOpen(1)
      }
      override def decorateBlockQuoteClose() = {
        inBlockQuote = false
        decorateHeaderClose(1)
      }
      override def decorateStrong(text:String) = s"<b>$text</b>"
      override def decorateUListOpen() = "<ul>"
      override def decorateUListClose() = "</ul>\n"
      override def decorateOListOpen() = "<ol>"
      override def decorateOListClose() = "</ol>\n"
      override def decorateItemOpen() = "<li>"
      override def decorateItemClose() = "</li>\n"
      override def decorateImg(alt:String, src:String, title:Option[String]) = ""
      override def decorateXml(xml:String) = xml.replaceAll("<.*?>", "")
    }
    fixLineBreaks(transformer(md))
  }
  
  private def fixLineBreaks(s:String) = {
    var res = s
    val tags = Seq("p","ul","ol","blockquote","codeblock", "section")
    val combs = tags.flatMap(t => tags.map(_ -> t))
    for (c <- combs) {
      res = res.replaceAll(s"</${c._1}><${c._2}>",s"</${c._1}>\n<${c._2}>")
      res = res.replaceAll(s"</${c._1}>\n<${c._2}>",s"</${c._1}>\n\n<${c._2}>")
    }
    res.replaceAll("</li>\n</ol>","</li></ol>").replaceAll("</li>\n</ul>","</li></ul>")
  }
  
  def toText(md:String):String = {
    val v = new BaseVisitor {
      var result: StringBuffer = new StringBuffer
      
      override def visit(node: ParaNode) {
        visitChildren(node)
      }
      override def visit(rn: RootNode) {
        visitChildren(rn)
      }
      override def visit(hn: HeaderNode) {
        visitChildren(hn)
      }
      override def visit(tn: TextNode) {
        println(s"visit textnode called with ${tn.getText}")
        result = result.append(tn.getText + "\n")
      }
    }
    val root = parseRoot(md)
    root.accept(v)
    v.result.toString
  }
}

class BaseVisitor extends Visitor {
  
  protected def visitChildren(node:Node) {
    for (child <- node.getChildren()) {
      child.accept(this);
    }
  }
  
  override def visit(node: AbbreviationNode) {
    visitChildren(node)
  }

  override def visit(node: AutoLinkNode) {visitChildren(node)}

  override def visit(node: BlockQuoteNode) {visitChildren(node)}

  override def visit(node: BulletListNode) {visitChildren(node)}

  override def visit(node: CodeNode) {visitChildren(node)}

  override def visit(node: DefinitionListNode) {visitChildren(node)}

  override def visit(node: DefinitionNode) {visitChildren(node)}

  override def visit(node: DefinitionTermNode) {visitChildren(node)}

  override def visit(node: StrikeNode) {visitChildren(node)}

  override def visit(node: AnchorLinkNode) {visitChildren(node)}
  
  override def visit(node: StrongEmphSuperNode) {visitChildren(node)}
  
  override def visit(node: TableCaptionNode) {visitChildren(node)}
  
  override def visit(node: ExpImageNode) {visitChildren(node)}

  override def visit(node: ExpLinkNode) {visitChildren(node)}

  override def visit(node: HeaderNode) {visitChildren(node)}

  override def visit(node: HtmlBlockNode) {visitChildren(node)}

  override def visit(node: InlineHtmlNode) {visitChildren(node)}

  override def visit(node: ListItemNode) {visitChildren(node)}

  override def visit(node: MailLinkNode) {visitChildren(node)}

  override def visit(node: Node) {visitChildren(node)}

  override def visit(node: OrderedListNode) {visitChildren(node)}

  override def visit(node: ParaNode) {visitChildren(node)}

  override def visit(node: QuotedNode) {visitChildren(node)}

  override def visit(node: ReferenceNode) {visitChildren(node)}

  override def visit(node: RefImageNode) {visitChildren(node)}

  override def visit(node: RefLinkNode) {visitChildren(node)}

  override def visit(node: RootNode) {visitChildren(node)}

  override def visit(node: SimpleNode) {visitChildren(node)}

  override def visit(node: SpecialTextNode) {visitChildren(node)}

  override def visit(node: SuperNode) {visitChildren(node)}

  override def visit(node: TableBodyNode) {visitChildren(node)}

  override def visit(node: TableCellNode) {visitChildren(node)}

  override def visit(node: TableColumnNode) {visitChildren(node)}

  override def visit(node: TableHeaderNode) {visitChildren(node)}

  override def visit(node: TableNode) {visitChildren(node)}

  override def visit(node: TableRowNode) {visitChildren(node)}

  override def visit(node: TextNode) {visitChildren(node)}

  override def visit(node: VerbatimNode) {visitChildren(node)}

  override def visit(node: WikiLinkNode) {visitChildren(node)}

}