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
import org.json4s.JsonAST._

class Timelimit (val group: Option[String],
                 val seconds: Double,
                 val timeoutNotice: Boolean) {
}

object Timelimit {
  def parse(obj: JObject, logger: ParserLogger): Option[Timelimit] = {
    val group = obj \ "group" match {
      case JNull => None
      case JString(str) => Some(str)
      case _ => {
        logger("Timelimit group attribute specified but of wrong type.")
        return None
      }
    }

    val seconds = obj \ "seconds" match {
      case JInt(s: BigInt) => s.toDouble
      case JDouble(s: Double) => s
      case _ => {
        logger("Timelimit seconds attribute specified but of wrong type.")
        return None
      }
    }

    val timeoutNotice = obj \ "timeoutnotice" match {
      case JBool(b: Boolean) => b
      case _ => {
        logger("Timelimit timeoutnotice attribute specified but of wrong type.")
        return None
      }
    }

    Some(new Timelimit(group, seconds, timeoutNotice))
  }
}