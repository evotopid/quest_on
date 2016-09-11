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