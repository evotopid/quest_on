package com.leoschwarz.quest_on.data

import java.sql.Blob

class Image(var id: Int,
            val surveyId: String,
            val location: ImageLocation,
            val blob: Option[Blob]) {
}
