// Copyright 2014-2016 Leonardo Schwarz (leoschwarz.com)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.leoschwarz.quest_on.data

import com.leoschwarz.quest_on.data.survey_ast.{ImageMessage, SurveyAST}
import org.json4s.jackson.JsonMethods

import scala.collection.mutable.ArrayBuffer

class Survey(var id: String,
             val adminId: Int,
             var data: String) {

  lazy val ast = SurveyAST.parse(data)

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

  def dataPretty: String = {
    JsonMethods.pretty(JsonMethods.parse(data))
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
    regex.findFirstIn(str).isEmpty && !str.isEmpty
  }
}
