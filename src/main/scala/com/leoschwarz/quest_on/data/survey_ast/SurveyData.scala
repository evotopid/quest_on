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

package com.leoschwarz.quest_on.data.survey_ast

import com.fasterxml.jackson.core.JsonParseException
import org.json4s.JsonAST.{JArray, JObject, JString}
import org.json4s.jackson.JsonMethods

import scala.collection.mutable.ArrayBuffer

class SurveyData (val title: String,
                  val pages: IndexedSeq[Page]) {

}

object SurveyData {
  def parse(rawData: String): Either[SurveyData, ParserError] = {
    val logger = new ParserLogger

    val json = try {
      JsonMethods.parse(rawData)
    } catch {
      case e: JsonParseException => {
        logger("JSON parsing failed.")
        return Right(ParserError(logger))
      }
    }

    val title = json \ "title" match {
      case JString(value) => value
      case _ => {
        logger("Parsing survey title failed.")
        return Right(ParserError(logger))
      }
    }
    val pages = json \ "pages" match {
      case JArray(objects) => {
        val buffer = new ArrayBuffer[Page](objects.length)
        for (obj <- objects) obj match {
          case o: JObject => {
            val page = Page.parse(o, logger)
            if (page.isEmpty) {
              logger("Parsing page failed.")
              return Right(ParserError(logger))
            } else {
              buffer += page.get
            }
          }
          case _ => {
            logger("Survey page is of wrong JSON type.")
            return Right(ParserError(logger))
          }
        }
        buffer
      }
      case _ => {
        logger("Survey's 'page' value is not of type object.")
        return Right(ParserError(logger))
      }
    }
    Left(new SurveyData(title, pages))
  }
}
