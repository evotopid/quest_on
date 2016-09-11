package com.leoschwarz.quest_on.data.survey_ast

import org.json4s.JObject

import scala.collection.mutable.ArrayBuffer

/**
  * Maintains the error log of parsing results.
  */
class ParserLogger {
  val messages = new ArrayBuffer[String]

  def apply(message: String): Unit = {
    messages += message
  }

  def failPageItem(message: String, item: JObject): Unit = {
    messages += s"FAIL PageItem: $message (item: $item)"
  }

  override def toString: String = {
    val result = new StringBuilder
    for (message <- messages) {
      result ++= message + "\n"
    }
    result.toString()
  }
}
