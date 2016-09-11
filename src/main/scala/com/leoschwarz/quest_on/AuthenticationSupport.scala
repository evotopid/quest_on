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

import com.leoschwarz.quest_on.data.Admin
import org.scalatra.ScalatraBase
import org.scalatra.auth.{ScentryConfig, ScentrySupport}
import org.scalatra.auth.strategy.BasicAuthSupport

trait AuthenticationSupport extends ScentrySupport[Admin] with BasicAuthSupport[Admin] {
  self: ScalatraBase with DatabaseAccess =>

  val realm = "quest_on admin"

  protected def fromSession = {
    case id: String => db.getAdminById(id.toInt).get
  }

  protected def toSession = {
    case user: Admin => user.id.toString
  }

  protected val scentryConfig = new ScentryConfig {}.asInstanceOf[ScentryConfiguration]

  override protected def configureScentry(): Unit = {
    scentry.unauthenticated {
      scentry.strategies("Basic").unauthenticated()
    }
  }

  override protected def registerAuthStrategies(): Unit = {
    scentry.register("Basic", app => new QuestOnAuthStrategy(self, realm))
  }
}
