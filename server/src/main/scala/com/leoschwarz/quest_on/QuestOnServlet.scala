package com.leoschwarz.quest_on

import java.io.File

import com.sun.xml.internal.ws.api.WebServiceFeatureFactory
import org.scalatra._
import org.scalatra.json._


class QuestOnServlet extends QuestOnStack {
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
