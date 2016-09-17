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

import org.json4s.{JArray, JObject}
import org.json4s.JsonAST.JString

import scala.collection.mutable

sealed trait PageItem

case class TextMessage(content: String) extends PageItem
case class TextInput(id: String) extends PageItem
case class ImageMessage(path: String) extends PageItem
case class MultipleChoiceInput(id: String, answers: mutable.HashMap[String, String]) extends PageItem

object PageItem {
  def parse(obj: JObject, logger: ParserLogger): Option[PageItem] = {
    val kind = obj \ "type" match {
      case JString(str) => str
      case _ => {
        logger.failPageItem("Attribute 'type' of wrong type.", obj)
        return None
      }
    }

    kind match {
      case "textmessage" => {
        val content = obj \ "content" match {
          case JString(str) => str
          case _ => {
            logger.failPageItem("TextMessage: no 'content' string.", obj)
            return None
          }
        }
        Some(TextMessage(content))
      }
      case "textinput" => {
        val id = obj \ "id" match {
          case JString(str) => str
          case _ => {
            logger.failPageItem("TextInput: no 'id' string.", obj)
            return None
          }
        }
        Some(TextInput(id))
      }
      case "image" => {
        val path = obj \ "path" match {
          case JString(str) => str
          case _ => {
            logger.failPageItem("Image: no 'path' string.", obj)
            return None
          }
        }
        Some(ImageMessage(path))
      }
      case "multiplechoice" => {
        val id = obj \ "id" match {
          case JString(str) => str
          case _ => {
            logger.failPageItem("MultipleChoice: no 'id' string.", obj)
            return None
          }
        }

        val answers = new mutable.HashMap[String, String]()
        obj \ "answers" match {
          case arr: JArray => {
            for (el <- arr) el match {
              case answer: JObject => {
                val value = answer \ "value" match {
                  case JString(str) => str,
                  case _ => {
                    logger.failPageItem("MultipleChoice: one answer doesn't have value string", obj)
                    return None
                  }
                }
                val text = answer \ "text" match {
                  case JString(str) => str,
                  case _ => {
                    logger.failPageItem("MultipleChoice: one answer doesn't have 'text' string", obj)
                    return None
                  }
                }
                answers += ((value, text))
              }
              case _ => {
                logger.failPageItem("MultipleChoice: one answer is not an object.", obj)
                return None
              }
            }
          },
          case _ => {
            logger.failPageItem("MultipleChoice: no 'answers' object.", obj)
            return None
          }
        }

        Some(MultipleChoiceInput(id, answers))
      }
      case _ => {
        logger.failPageItem("Invalid item type.", obj)
        None
      }
    }
  }
}