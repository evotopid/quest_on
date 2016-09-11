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
