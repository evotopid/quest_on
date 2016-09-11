package com.leoschwarz.quest_on.data

import com.leoschwarz.quest_on.data.survey_ast.{ImageMessage, SurveyData}
import scala.collection.mutable.ArrayBuffer

class Survey(var id: String,
             val adminId: Int,
             var data: String) {

  lazy val ast = SurveyData.parse(data)

  def isValid = ast.isLeft

  def title: String = {
    ast match {
      case Left(a) => a.title
      case Right(_) => "[no title]"
    }
  }

  def allReferencedImages: ArrayBuffer[String] = {
    val result = new ArrayBuffer[String]()
    ast match {
      case Left(data) => {
        for (page <- data.pages) {
          for (item <- page.items) item match {
            case ImageMessage(path: String) => result += path
            case _ =>
          }
        }
      }
      case _ =>
    }

    result
  }
}

object Survey {
  /**
    * Check if an id is a valid survey id.
    *
    * @param str id to check
    * @return true if and only if valid
    */
  def isValidId(str: String): Boolean = {
    val regex = "[^a-zA-Z0-9_-]".r
    regex.findFirstIn(str).isEmpty
  }
}
