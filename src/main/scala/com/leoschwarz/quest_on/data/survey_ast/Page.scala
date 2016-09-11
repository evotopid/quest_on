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

import org.json4s.JsonAST.{JArray, JNull, JObject, JString}

import scala.collection.mutable.ArrayBuffer

class Page(val items: IndexedSeq[PageItem],
           val timelimit: Option[Timelimit],
           val actions: IndexedSeq[String]) {

}

object Page {
  def parse(pageData: JObject, logger: ParserLogger): Option[Page] = {
    val actions = pageData \ "actions" match {
      case JArray(values) => {
        val buffer = new ArrayBuffer[String](values.length)
        for (value <- values) value match {
          case JString(v) => buffer += v
          case _ => {
            logger("Page action is of invalid type (must be string).")
            return None
          }
        }
        buffer
      }
      case _ => {
        logger("Page actions attribute is of invalid type (must be array).")
        return None
      }
    }

    val timelimit = pageData \ "timelimit" match {
      case obj: JObject => {
        val limit = Timelimit.parse(obj, logger)
        if (limit.isEmpty) {
          logger("Parsing timelimit failed.")
          return None
        }
        limit
      }
      case _ => None
    }

    val items = pageData \ "items" match {
      case JArray(items) => {
        val buffer = new ArrayBuffer[PageItem](items.length)
        for (item <- items) item match {
          case obj: JObject => {
            val i = PageItem.parse(obj, logger)
            if (i.isEmpty) {
              logger("Failed parsing pageitem.")
              return None
            }
            buffer += i.get
          }
          case _ => {
            logger("Page item is of wrong type. (must be json object)")
            return None
          }
        }
        buffer
      }
      case _ => {
        logger("Page 'items' attribute is of wrong kind. (must be array)")
        return None
      }
    }

    Some(new Page(items, timelimit, actions))
  }
}