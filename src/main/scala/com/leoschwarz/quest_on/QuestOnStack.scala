package com.leoschwarz.quest_on

import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import scalate.ScalateSupport
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.servlet.{FileUploadSupport, MultipartConfig}


trait QuestOnStack extends ScalatraServlet
  with ScalateSupport with JacksonJsonSupport with SessionSupport with FlashMapSupport with FileUploadSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  notFound {
    // remove content type in case it was set through an action
    contentType = null
    // Try to render a ScalateTemplate if no route matched
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound()
  }

  configureMultipartHandling(MultipartConfig(maxFileSize = Some(2*1024*1024)))
}
