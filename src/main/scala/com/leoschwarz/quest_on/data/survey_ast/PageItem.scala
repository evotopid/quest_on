package com.leoschwarz.quest_on.data.survey_ast

import org.json4s.JObject
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
          case JObject(elements) => {
            for (el <- elements) {
              answers += ((el._1, el._2 match {
                case JString(str) => str
                case _ => {
                  logger.failPageItem("MultipleChoice: an answer value is not a string.", obj)
                  return None
                }
              }))
            }
          }
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