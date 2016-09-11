package com.leoschwarz.quest_on.data

class Image(var id: Int,
            val surveyId: String,
            val filename: String,
            val mimeType: Option[String],
            var location: ImageLocation,
            val blob: Option[Array[Byte]]) {
}
