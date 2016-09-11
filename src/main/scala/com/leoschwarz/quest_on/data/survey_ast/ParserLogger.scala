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
