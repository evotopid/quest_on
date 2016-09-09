package com.leoschwarz.quest_on

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.leoschwarz.quest_on.data.Admin
import org.scalatra.ScalatraBase
import org.scalatra.auth.strategy.BasicAuthStrategy

class QuestOnAuthStrategy(protected override val app: ScalatraBase with DatabaseAccess,
                          protected val realm: String)
  extends BasicAuthStrategy[Admin](app, realm) {


  override protected def getUserId(user: Admin)
                                  (implicit request: HttpServletRequest,
                                   response: HttpServletResponse): String = {
    user.id.toString
  }

  override protected def validate(email: String, password: String)
                                 (implicit request: HttpServletRequest,
                                  response: HttpServletResponse): Option[Admin] = {
    app.db.getAdminByEmail(email) match {
      case Some(admin) => if (admin.checkPassword(password)) {
        Some(admin)
      } else {
        None
      }
      case None => None
    }
  }
}
