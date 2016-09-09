package com.leoschwarz.quest_on

import java.io.File
import java.sql.Timestamp

import com.leoschwarz.quest_on.data.{Admin, Database, Result, Survey}
import org.scalatra.{BadRequest, InternalServerError, NotFound, Ok}

class QuestOnServlet extends QuestOnStack with DatabaseAccess with AuthenticationSupport {
  val AdminLayout = "WEB-INF/templates/layouts/admin.ssp"
  val db: Database = Database.getDefault()

  get("/") {
    ssp("/index")
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

  get("/all_images.json") {
    contentType = formats("json")
    val imageDir = new File(getServletContext.getRealPath("/img"))
    imageDir.listFiles().map((f) => s"/img/${f.getName}").toList
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
    ssp("/admin/register", "layout" -> AdminLayout)
  }

  post("/admin/register") {
    val email = params("email")
    val password = params("password")

    // Validate for emptiness.
    if (email.isEmpty || password.isEmpty) {
      ssp("/admin/register", "layout" -> AdminLayout, "formError" -> "Email and password mustn't be empty.")
    } else if (db.getAdminByEmail(email).isDefined) {
      ssp("/admin/register", "layout" -> AdminLayout, "formError" -> "Email already taken.")
    } else {
      // Create admin in database
      val admin = Admin.create(email, password)
      db.insert(admin)

      // Redirect to login page.
      flash("notice") = "You have been registered successfully."
      redirect("/admin/dashboard")
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

  get("/admin/survey/edit/:id") {
    val (admin, survey) = adminSurveyAuth.get
    ssp("/admin/survey_edit.ssp", "layout" -> AdminLayout, "survey" -> survey)
  }

  post("/admin/survey/edit/:id") {
    val (admin, survey) = adminSurveyAuth.get

    survey.data = params("data")
    db.update(survey)
    redirect("/admin/dashboard")
  }

  get("/admin/survey/results/:id") {
    val (admin, survey) = adminSurveyAuth.get
  }

  get("/admin/survey/images/:id") {
    val (admin, survey) = adminSurveyAuth.get
    val images = db.getImagesOfSurvey(survey.id)
    ssp("/admin/survey_images.ssp", "layout" -> "admin", "images" -> images)
  }

  /**
    * Makes sure an admin is authenticated.
    * If this is not the case the browser will be redirected to the login page and the current execution will be halted.
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
