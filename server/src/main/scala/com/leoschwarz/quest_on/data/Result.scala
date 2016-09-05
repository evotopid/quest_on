package com.leoschwarz.quest_on.data

import java.sql.Timestamp

class Result (var id: Option[Int],
              val surveyId: String,
              val submittedAt: Timestamp,
              val data: String) {


  override def toString: String =
    s"id: $id\nsurveyId: $surveyId\nsubmittedAt: $submittedAt\ndata: $data"
}