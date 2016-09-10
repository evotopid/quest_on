package com.leoschwarz.quest_on.data

import java.io.File
import java.net.URL
import java.sql.{Blob, Connection, DriverManager, ResultSet, Timestamp}

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

class Database(connection: Connection, schemaSetup: Array[String]) {
  // Setup tables if not present
  for (query <- schemaSetup) {
    exec(query)
  }

  def deleteImage(id: Int): Unit = {
    val query = "DELETE FROM images WHERE id = ?"
    val stmt = connection.prepareStatement(query)
    stmt.setInt(1, id)
    stmt.execute()
  }

  def delete(image: Image): Unit = {
    deleteImage(image.id)
  }

  def exec(query: String): Boolean = {
    val stmt = connection.createStatement()
    stmt.execute(query)
  }

  def exec(file: File): Boolean = exec(Source.fromFile(file).mkString)
  def exec(url: URL): Boolean = exec(Source.fromURL(url).mkString)

  // Insert and use existing id
  def insert(survey: Survey): Unit = {
    val query = "INSERT INTO surveys (id, admin_id, data) VALUES (?, ?, ?)"
    val stmt = connection.prepareStatement(query)
    stmt.setString(1, survey.id)
    stmt.setInt(2, survey.adminId)
    stmt.setString(3, survey.data)
    stmt.execute()
  }

  def insert(result: Result): Unit = {
    val query = "INSERT INTO results (survey_id, submitted_at, data) VALUES (?, ?, ?)"
    val stmt = connection.prepareStatement(query)
    stmt.setString(1, result.surveyId)
    stmt.setString(2, result.submittedAt.toString)
    stmt.setString(3, result.data)
    stmt.execute()
    result.id = Some(stmt.getGeneratedKeys.getInt(1))
  }

  def insert(admin: Admin): Unit = {
    val query = "INSERT INTO admins (email, password_salt, password_hash) VALUES (?, ?, ?)"
    val stmt = connection.prepareStatement(query)
    stmt.setString(1, admin.email)
    stmt.setString(2, admin.passwordSalt)
    stmt.setString(3, admin.passwordHash)
    stmt.execute()
    admin.id = stmt.getGeneratedKeys.getInt(1)
  }

  def insert(image: Image): Unit = {
    val query = "INSERT INTO images (survey_id, location, blob, filename, mime_type) VALUES (?, ?, ?, ?, ?)"
    val stmt = connection.prepareStatement(query)
    stmt.setString(1, image.surveyId)
    stmt.setString(2, image.location.toString)
    stmt.setBytes(3, image.blob.orNull)
    stmt.setString(4, image.filename)
    stmt.setString(5, image.mimeType.orNull)
    stmt.execute()
    image.id = stmt.getGeneratedKeys.getInt(1)
  }

  def insertOrUpdate(image: Image): Unit = {
    if (getImageById(image.id).isDefined) {
      update(image)
    } else {
      insert(image)
    }
  }

  def update(survey: Survey): Unit = {
    val query = "UPDATE surveys SET data = ? WHERE id = ?"
    val stmt = connection.prepareStatement(query)
    stmt.setString(1, survey.data)
    stmt.setString(2, survey.id)
    stmt.execute()
  }

  def update(image: Image): Unit = {
    val query = "UPDATE images SET survey_id = ? location = ?, blob = ?, filename = ?, mime_type = ? WHERE id = ?"
    val stmt = connection.prepareStatement(query)
    stmt.setString(1, image.surveyId)
    stmt.setString(2, image.location.toString)
    stmt.setBytes(3, image.blob.orNull)
    stmt.setString(4, image.filename)
    stmt.setString(5, image.mimeType.orNull)
    stmt.setInt(6, image.id)
    stmt.execute()
  }

  def getImageById(id: Int): Option[Image] = {
    val query = "SELECT * FROM images WHERE id = ?"
    val stmt = connection.prepareStatement(query)
    stmt.setInt(1, id)
    val result = stmt.executeQuery()
    if (result.next()) {
      Some(extractImage(result))
    } else {
      None
    }
  }

  def getImageBySurveyIdAndFilename(surveyId: String, filename: String): Option[Image] = {
    val query = "SELECT * FROM images WHERE survey_id = ? and filename = ?"
    val stmt = connection.prepareStatement(query)
    stmt.setString(1, surveyId)
    stmt.setString(2, filename)
    val result = stmt.executeQuery()
    if (result.next()) {
      Some(extractImage(result))
    } else {
      None
    }
  }

  def getImagesOfSurvey(surveyId: String): ArrayBuffer[Image] = {
    val query = "SELECT * FROM images WHERE survey_id = ?"
    val stmt = connection.prepareStatement(query)
    stmt.setString(1, surveyId)
    val result = stmt.executeQuery()
    val images = new ArrayBuffer[Image]()
    while (result.next()) {
      images += extractImage(result)
    }
    images
  }

  private def extractImage(result: ResultSet): Image = {
    new Image(
      id = result.getInt("id"),
      surveyId = result.getString("survey_id"),
      filename = result.getString("filename"),
      mimeType = Option(result.getString("mime_type")),
      location = ImageLocation.fromString(result.getString("location")).get,
      blob = Option(result.getBytes("blob"))
    )
  }

  def getSurveyById(id: String): Option[Survey] = {
    val query = "SELECT * FROM surveys WHERE id = ?"
    val stmt = connection.prepareStatement(query)
    stmt.setString(1, id)
    val result = stmt.executeQuery()
    if (result.next()) {
      Some(extractSurvey(result))
    } else {
      None
    }
  }

  def getSurveysOfAdmin(admin: Admin): ArrayBuffer[Survey] = {
    val query = "SELECT * FROM surveys WHERE admin_id = ?"
    val stmt = connection.prepareStatement(query)
    stmt.setInt(1, admin.id)
    val result = stmt.executeQuery()
    val surveys = new ArrayBuffer[Survey]()
    while (result.next()) {
      surveys += extractSurvey(result)
    }
    surveys
   }

  private def extractSurvey(result: ResultSet): Survey = {
    val id = result.getString("id")
    val admin_id = result.getInt("admin_id")
    val data = result.getString("data")
    new Survey(id, admin_id, data)
  }

  def getAdminById(id: Int): Option[Admin] = {
    val query = "SELECT * FROM admins WHERE id = ?"
    val stmt = connection.prepareStatement(query)
    stmt.setInt(1, id)
    adminFromResultSet(stmt.executeQuery())
  }

  def getAdminByEmail(email: String): Option[Admin] = {
    val query = "SELECT * FROM admins WHERE email = ?"
    val stmt = connection.prepareStatement(query)
    stmt.setString(1, email)
    adminFromResultSet(stmt.executeQuery())
  }

  private def adminFromResultSet(result: ResultSet) = {
    if (result.next()) {
      Some(new Admin(result.getInt("id"),
        result.getString("email"),
        result.getString("password_salt"),
        result.getString("password_hash")))
    } else {
      None
    }
  }
}

object Database {
  val SchemaSqlite = Array(
    "CREATE TABLE IF NOT EXISTS surveys (id TEXT PRIMARY KEY, admin_id INT, data TEXT)",
    "CREATE TABLE IF NOT EXISTS admins  (id INTEGER PRIMARY KEY, email TEXT, password_salt TEXT, password_hash TEXT)",
    "CREATE TABLE IF NOT EXISTS results (id INTEGER PRIMARY KEY AUTOINCREMENT, survey_id TEXT, submitted_at TIMESTAMP WITH TIME ZONE, data TEXT)",
    "CREATE TABLE IF NOT EXISTS images  (id INTEGER PRIMARY KEY AUTOINCREMENT, survey_id TEXT, filename TEXT, mime_type TEXT, location TEXT, blob BLOB)"
  )

  val SchemaPostgres = Array(
    "CREATE TABLE IF NOT EXISTS surveys (id VARCHAR(64) PRIMARY KEY, admin_id INT, data TEXT)",
    "CREATE TABLE IF NOT EXISTS admins  (id SERIAL PRIMARY KEY, email TEXT, password_salt TEXT, password_hash TEXT)",
    "CREATE TABLE IF NOT EXISTS results (id SERIAL PRIMARY KEY, survey_id VARCHAR(64), submitted_at TIMESTAMP WITH TIME ZONE, data TEXT)",
    "CREATE TABLE IF NOT EXISTS images  (id SERIAL PRIMARY KEY, survey_id VARCHAR(64), filename TEXT, mime_type VARCHAR(255), location TEXT, blob BLOB)"
  )

  def getDefault(): Database = {
    // Try accessing Heroku database
    sys.env.get("JDBC_DATABASE_URL") match {
      case Some(url) => new Database(DriverManager.getConnection(url), SchemaPostgres)
      case None => {
        System.err.println("No JDBC Database connection was found. (This mustn't happen on Heroku)")
        System.err.println("As fallback SQLite will be used")

        val location = "./test.sqlite"
        val db = new Database(DriverManager.getConnection(s"jdbc:sqlite:$location", "sa", ""), SchemaSqlite)

        // Create test survey if doesn't exist yet.
        if (db.getSurveyById("TEST_SURVEY").isEmpty) {
          val survey = new Survey("TEST_SURVEY", 0, Source.fromURL(getClass.getResource("/survey_test.json")).mkString)
          db.insert(survey)
        }

        db
      }
    }
  }
}