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
