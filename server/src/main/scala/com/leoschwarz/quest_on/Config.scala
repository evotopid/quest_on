package com.leoschwarz.quest_on

import org.yaml.snakeyaml.Yaml

import scala.io.Source

class Config (val registrationEnabled: Boolean) {

}

object Config {
  private val DefaultRegistrationEnabled = false

  lazy val get = {
    val rawData = Source.fromURL(getClass.getResource("/config.yaml")).mkString
    val yaml = new Yaml()
    yaml.load(rawData) match {
      case data: java.util.Map[String, Any] => {
        val registrationEnabled = data.getOrDefault("registration_enabled", DefaultRegistrationEnabled) == true
        new Config(registrationEnabled)
      }
      case _ => {
        sys.error("Was not able to load configuration file.")
        sys.error("Using defaults right now.")
        new Config(DefaultRegistrationEnabled)
      }
    }
  }
}
