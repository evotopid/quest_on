package com.leoschwarz.quest_on

import java.io.File
import javax.servlet.ServletConfig

import com.leoschwarz.quest_on.data.Database
import org.scalatra.NotFound

class QuestOnServlet extends QuestOnStack {
  var db: Database = null

  override def init(config: ServletConfig): Unit = {
    super.init(config)

    db = Database.getDefault()
    db.exec(getClass.getResource("/database.sql"))
  }

  post("/store") {
    val results = params("results")
    // TODO store in database
  }

  get("/survey/:id.json") {
    db.getSurveyById(params("id")) match {
      case Some(survey) => {
        contentType = "application/javascript"
        survey.data
      }
      case None => NotFound("Survey was not found on server.")
    }
  }

  get("/strings.json") {
    contentType = "application/javascript"

    val defaultLanguage = "en"
    val requestedLanguage = Option(request.getHeader("HTTP_ACCEPT_LANGUAGE")) match {
      case Some(value) => value.take(2)
      case None => defaultLanguage
    }

    val requestedFile = new File(getServletContext.getRealPath("/WEB-INF/locales/" + requestedLanguage + ".json"))
    val defaultFile   = new File(getServletContext.getRealPath("/WEB-INF/locales/" + defaultLanguage + ".json"))
    if (requestedFile.exists()) {
      requestedFile
    } else {
      defaultFile
    }
  }

  get("/all_images.json") {
    contentType = formats("json")
    val imageDir = new File(getServletContext.getRealPath("/img"))
    imageDir.listFiles().map((f) => s"/img/${f.getName}").toList
  }
}
