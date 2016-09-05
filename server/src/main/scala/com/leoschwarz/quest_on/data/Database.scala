package com.leoschwarz.quest_on.data

import java.io.File
import java.net.URL
import java.sql.{Connection, DriverManager, ResultSet}

import scala.io.Source

class Database(connection: Connection) {
  def exec(query: String): Boolean = {
    val stmt = connection.createStatement()
    stmt.execute(query)
  }

  def exec(file: File): Boolean = exec(Source.fromFile(file).mkString)
  def exec(url: URL): Boolean = exec(Source.fromURL(url).mkString)

  // Insert and use existing id
  def insert(survey: Survey): Boolean = {
    val query = "INSERT INTO surveys (id, admin_id, data) VALUES (?, ?, ?)"
    val stmt = connection.prepareStatement(query)
    stmt.setString(1, survey.id)
    stmt.setInt(2, survey.admin_id)
    stmt.setString(3, survey.data)
    stmt.execute()
  }

  def getSurveyById(id: String): Option[Survey] = {
    val query = "SELECT * FROM surveys WHERE id = ?"
    val stmt = connection.prepareStatement(query)
    stmt.setString(1, id)
    val result = stmt.executeQuery()
    if (result.next()) {
      val id = result.getString("id")
      val admin_id = result.getInt("admin_id")
      val data = result.getString("data")
      Some(new Survey(id, admin_id, data))
    } else {
      None
    }
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
  def getDefault(): Database = {
    // Try accessing Heroku database
    sys.env.get("JDBC_DATABASE_URL") match {
      case Some(url) => new Database(DriverManager.getConnection(url))
      case None => {
        System.err.println("No JDBC Database connection was found. (This mustn't happen on Heroku)")
        System.err.println("As fallback SQLite will be used")

        val location = "./test.sqlite"
        val db = new Database(DriverManager.getConnection(s"jdbc:sqlite:$location", "sa", ""))

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