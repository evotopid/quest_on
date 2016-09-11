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
