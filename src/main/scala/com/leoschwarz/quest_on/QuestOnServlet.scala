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

import java.io.File
import java.sql.Timestamp

import com.leoschwarz.quest_on.data._
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.scalatra.{BadRequest, MovedPermanently, NotFound, Ok}

import scala.collection.mutable
import scala.util.control.Breaks

class QuestOnServlet extends QuestOnStack with DatabaseAccess with AuthenticationSupport {
  val AdminLayout = "WEB-INF/templates/layouts/admin.ssp"
  val db: Database = Database.getDefault()

  get("/") {
    ssp("/index")
  }

  get("/survey/:survey_id/img/:filename") {
    db.getImageBySurveyIdAndFilename(params("survey_id"), params("filename")) match {
      case Some(image) => {
        image.location match {
          case Local() => {
            contentType = image.mimeType.getOrElse("")
            image.blob.get
          }
          case Remote(url) => MovedPermanently(url)
        }
      }
      case _ => NotFound
    }
  }

  get("/survey/:id") {
    db.getSurveyById(params("id")) match {
      case Some(survey) => {
        ssp("/survey", "survey_id" -> params("id"))
      }
      case None => NotFound("Survey was not found on server.")
    }
  }

  post("/store") {
    val result = new Result(None,
      surveyId = params("surveyId"),
      new Timestamp(System.currentTimeMillis()),
      data = params("results"))

    log(s"saving survey result: $result")

    //if (db.insert(result)) {
    db.insert(result)
    Ok("Survey results stored.")
    //} else {
    //  InternalServerError("Saving results failed.")
    //}
  }

  get("/survey/:id.json") {
    db.getSurveyById(params("id")) match {
      case Some(survey) => {
        contentType = formats("json")
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
    val defaultFile = new File(getServletContext.getRealPath("/WEB-INF/locales/" + defaultLanguage + ".json"))
    if (requestedFile.exists()) {
      requestedFile
    } else {
      defaultFile
    }
  }

  get("/survey/:id/images.json") {
    contentType = formats("json")
    val images = db.getImagesOfSurvey(params("id"))
    images.map((image) => s"/survey/${image.surveyId}/img/${image.filename}").toList
  }

  /* ***************************************
  ADMIN AREA
   */
  get("/admin") {
    redirect("/admin/dashboard")
  }

  get("/admin/dashboard") {
    val admin = adminAuth()
    val surveys = db.getSurveysOfAdmin(admin.get)
    ssp("/admin/dashboard", "layout" -> AdminLayout, "surveys" -> surveys)
  }

  get("/admin/login") {
    ssp("/admin/login", "layout" -> AdminLayout)
  }

  post("/admin/login") {
    val password = params("password")
    val admin = db.getAdminByEmail(params("email"))
    if (admin.isDefined && admin.get.checkPassword(password)) {
      // Authentication successful.
      scentry.user = admin.get
      redirect("/admin/dashboard")
    } else {
      // Authentication failed
      ssp("/admin/login", "layout" -> AdminLayout)
    }
  }

  get("/admin/logout") {
    scentry.logout()
    redirect("/")
  }

  get("/admin/register") {
    ssp("/admin/register", "layout" -> AdminLayout, "registrationEnabled" -> Config.get.registrationEnabled)
  }

  post("/admin/register") {
    if (Config.get.registrationEnabled) {
      val email = params("email")
      val password = params("password")

      // Validate for emptiness.
      if (email.isEmpty || password.isEmpty) {
        ssp("/admin/register", "layout" -> AdminLayout,"formError" -> "Email and password mustn't be empty.",
          "registrationEnabled" -> Config.get.registrationEnabled)
      } else if (db.getAdminByEmail(email).isDefined) {
        ssp("/admin/register", "layout" -> AdminLayout, "formError" -> "Email already taken.",
          "registrationEnabled" -> Config.get.registrationEnabled)
      } else {
        // Create admin in database
        val admin = Admin.create(email, password)
        db.insert(admin)

        // Redirect to login page.
        flash("notice") = "You have been registered successfully."
        redirect("/admin/dashboard")
      }
    } else {
      BadRequest("Registration is disabled.")
    }
  }

  get("/admin/survey/new") {
    adminAuth()
    ssp("/admin/survey_new.ssp", "layout" -> AdminLayout)
  }

  post("/admin/survey/new") {
    // TODO proper survey validation?
    val admin = adminAuth().get

    // Make sure id is not taken already.
    val id = params("id")
    if (db.getSurveyById(id).isDefined) {
      ssp("/admin/survey_new.ssp", "layout" -> AdminLayout, "formError" -> "ID is already taken.")
    } else if (!Survey.isValidId(id)) {
      ssp("/admin/survey_new.ssp", "layout" -> AdminLayout, "formError" -> "Invalid characters in ID.")
    } else {
      // Store in the database.
      val survey = new Survey(id, admin.id, params("data"))
      db.insert(survey)
      redirect("/admin/dashboard")
    }
  }

  get("/admin/survey/:id/edit") {
    val (admin, survey) = adminSurveyAuth.get
    ssp("/admin/survey_edit.ssp", "layout" -> AdminLayout, "survey" -> survey)
  }

  post("/admin/survey/:id/edit") {
    val (admin, survey) = adminSurveyAuth.get

    survey.data = params("data")
    db.update(survey)
    redirect("/admin/dashboard")
  }

  get("/admin/survey/:id/results") {
    val (admin, survey) = adminSurveyAuth.get
    val resultCount = db.getResults(survey).length
    ssp("/admin/survey_results.ssp", "layout" -> AdminLayout, "survey" -> survey, "resultsCount" -> resultCount)
  }

  get("/admin/survey/:id/results.xlsx") {
    val (admin, survey) = adminSurveyAuth.get
    val results = db.getResults(survey)
    contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    new Exporter(results).export()
  }

  get("/admin/survey/:id/validate") {
    val (admin, survey) = adminSurveyAuth.get
    ssp("/admin/survey_validate.ssp", "layout" -> AdminLayout, "survey" -> survey)
  }

  get("/admin/survey/:id/images") {
    val (admin, survey) = adminSurveyAuth.get
    val images = db.getImagesOfSurvey(survey.id)

    // Find missing images.
    val missingImages = new mutable.HashSet[String]
    for (referencedImage <- survey.allReferencedImages) {
      Breaks.breakable {
        for (image <- images) {
          if (image.filename == referencedImage)
            Breaks.break()
        }

        missingImages += referencedImage
      }
    }

    ssp("/admin/survey_images.ssp", "layout" -> AdminLayout, "surveyId" -> survey.id,
      "images" -> images, "missingImages" -> missingImages)
  }

  get("/admin/survey/:id/images/add/:image") {
    val (admin, survey) = adminSurveyAuth.get
    ssp("/admin/survey_image_add.ssp", "layout" -> AdminLayout, "filename" -> params("image"))
  }

  post("/admin/survey/:id/images/add/:image") {
    val (admin, survey) = adminSurveyAuth.get
    val filename = params("filename")
    params.getOrElse("location", "") match {
      case "remote" => {
        val url = params("url")

        // Insert or update image. (TODO MIME Type)
        val image = new Image(-1, surveyId = survey.id, filename = filename, mimeType = None, location = Remote(url), blob = None)
        // TODO this is futile right now (also under local) since the id is -1...
        db.insertOrUpdate(image)

        redirect(s"/admin/survey/${survey.id}/images")
      }
      case "local" => {
        val file = fileParams("file")
        val mime = file.contentType
        val blob = Some(file.get())
        val image = new Image(-1, surveyId = survey.id, filename = filename, mimeType = mime, location = Local(), blob = blob)
        db.insertOrUpdate(image)

        redirect(s"/admin/survey/${survey.id}/images")
      }
      case _ => BadRequest("Invalid location.")
    }
  }

  get("/admin/survey/:id/images/edit/:image_id") {
    adminSurveyAuth
    val image = db.getImageById(params("image_id").toInt).get
    ssp("/admin/survey_image_edit.ssp", "layout" -> AdminLayout, "image" -> image)
  }

  post("/admin/survey/:id/images/edit/:image_id") {
    val (admin, survey) = adminSurveyAuth.get
    val image = db.getImageById(params("image_id").toInt).get

    if (image.surveyId != survey.id) {
      BadRequest("Wrong survey relation.")
    } else if (image.location.local.isDefined) {
      BadRequest("Not implemented yet.")
    } else {
      image.location = Remote(params("url"))
      db.update(image)
      redirect(s"/admin/survey/${survey.id}/images")
    }
  }

  get("/admin/survey/:id/images/delete/:image_id") {
    val (admin, survey) = adminSurveyAuth.get
    val image = db.getImageById(params("image_id").toInt).get
    if (image.surveyId != survey.id) {
      BadRequest("Wrong survey relation.")
    } else {
      db.delete(image)
      redirect(s"/admin/survey/${survey.id}/images")
    }
  }

  /**
    * Makes sure an admin is authenticated.
    * If this is not the case the browser will be redirected to the login page and the current execution will be halted.
    *
    * @return the authenticated admin
    */
  protected def adminAuth(): Option[Admin] = {
    if (!scentry.isAuthenticated) {
      redirect("/admin/login")
      halt(401, "Unauthenticated")
      None
    } else {
      Some(scentry.user)
    }
  }

  /**
    * Makes sure and admin is authenticated and has access to the survey with id params("id").
    * If this is not the case the browser will be redirected to the dashboard page and the current execution will be halted.
    *
    * @return authenticated admin, accessed survey
    */
  protected def adminSurveyAuth: Option[(Admin, Survey)] = {
    adminAuth match {
      case None => None
      case Some(admin) => {
        val surveyId = params("id")
        val survey = db.getSurveyById(surveyId)
        if (survey.isEmpty || survey.get.adminId != admin.id) {
          redirect("/admin/dashboard")
          halt(BadRequest("Invalid operation."))
          None
        } else {
          Some((admin, survey.get))
        }
      }
    }
  }
}
