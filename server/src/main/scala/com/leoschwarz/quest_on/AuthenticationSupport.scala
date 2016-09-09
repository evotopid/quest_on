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
