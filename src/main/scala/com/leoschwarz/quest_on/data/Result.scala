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

import java.sql.Timestamp

import org.json4s.JsonAST.{JArray, JObject, JString}
import org.json4s.jackson.JsonMethods

import scala.collection.mutable

class Result(var id: Option[Int],
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