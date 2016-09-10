package com.leoschwarz.quest_on.data

import java.sql.Timestamp

import org.json4s.JsonAST.{JArray, JObject, JString}
import org.json4s.jackson.JsonMethods

import scala.collection.mutable
import scala.util.Try

class Result (var id: Option[Int],
              val surveyId: String,
              val submittedAt: Timestamp,
              val data: String) {

  override def toString: String =
    s"id: $id\nsurveyId: $surveyId\nsubmittedAt: $submittedAt\ndata: $data"

  def parseData: Option[mutable.LinkedHashMap[String, String]] = {
    val result = new mutable.LinkedHashMap[String, String]
    JsonMethods.parse(data) match {
      case JArray(children) => {
        for (child <- children) child match {
          case pair: JObject => {
            result += ((pair.values("id").toString, pair.values.getOrElse("value", "").toString))
          }
          case _ => return None
        }
        Some(result)
      }
      case _ => None
    }
  }
}