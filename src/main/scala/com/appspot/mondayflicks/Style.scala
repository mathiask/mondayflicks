package com.appspot.mondayflicks

import org.scalatra.ScalatraKernel

import scala.xml.{XML, Node}
import scala.xml.dtd.{DocType, PublicID}

import java.io.StringWriter

trait Style {

  protected val style =
    """
    |body {
    |  font-family: Trebuchet MS, sans-serif;
    |  padding-left: 30px; padding-right: 30px; padding-top: 30px;
    |  min-width: 50ex; max-width: 100ex;
    |  margin-left: auto; margin-right: auto;
    |  background-color: white;
    |}
    |#main {
    |  padding: 2ex;
    |  background-color: #eeeeee;
    |  border: 2px solid #003b6b;
    |  border-radius: 8px; -moz-border-radius: 8px; -webkit-border-radius: 8px;
    |}
    |#content {
    |  min-height: 180px;
    |}
    |h1 { color: #005580; margin-top: 0px; }
    |h3 { margin: 0px; color: #003b6b; }
    |span.item { margin-right: 1em; }
    |a { color: #404040; }
    |a:hover, .editable-highlighted { background-color: #8ECAE8; }
    |input, textarea { border-radius: 4px; -moz-border-radius: 4px; -webkit-border-radius: 4px; }
    |div.user, span.tiny { color: gray; font-size: small; font-style: italic; font-weight: lighter; }
    |div.comment {
    |  padding: 4px;
    |  border: 2px solid #005580;
    |  border-radius: 4px; -moz-border-radius: 4px; -webkit-border-radius: 4px;
    |  width: 40em; margin-bottom: 2ex;
    |}
    |input.delete { float: right; }
    |div.sidebar {
    |  float: right;
    |  margin: 1ex;
    |  padding: 1ex;
    |  background-color: #e0e0e0;
    |  border: 1px solid #003b6b;
    |  border-radius: 8px; -moz-border-radius: 8px; -webkit-border-radius: 8px;
    |  width: 33%;
    |}
    |div.popup { position: absolute; top: 2ex; left: -1.5em; display: none; z-index: 1; }
    |div.error { font-size: large; color: red; }
    |img.icon { width: 16px; height: 16px; vertical-align: middle; margin-right: 0.5em;}
    |div.appengine, a.login { float: right; margin-left: 1ex; }
    """.stripMargin

  def asXHTMLWithDocType(doc: Node): String = {
      val writer = new StringWriter
      XML.write(writer, doc, "UTF-8", true, doctype)
      writer.toString
  }

  private val doctype = DocType("html", PublicID("-//W3C//DTD XHTML 1.0 Strict//EN",
                                                 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"), 
                                Nil)

}
