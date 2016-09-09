package com.leoschwarz.quest_on.data

import org.json4s.JsonAST.JString
import org.json4s.jackson.JsonMethods

class Survey (var id: String,
              val adminId: Int,
              var data: String) {

  def title: String = {
    try {
      val json = JsonMethods.parse(data)
      (json \ "title") match {
        case JString(s) => s
        case _ => "[no title]"
      }
    } catch {
      case _ => "[no title]"
    }
  }
}

object Survey {
  /**
    * Check if an id is a valid survey id.
    * @param str id to check
    * @return true if and only if valid
    */
  def isValidId(str: String): Boolean = {
    val regex = "[^a-zA-Z0-9_-]".r
    regex.findFirstIn(str).isEmpty
  }
}
