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

package com.leoschwarz.quest_on.data

import java.security._
import java.util.Base64
import javax.crypto._
import javax.crypto.spec._

class Admin (var id: Int,
             val email: String,
             var passwordSalt: String,
             var passwordHash: String) {

  private val HashIterations = 20000
  private val HashBytes = 64
  private val SaltBytes = 64

  def setPassword(passwordPlain: String): Unit = {
    // Generate new salt.
    val bytes = new Array[Byte](SaltBytes)
    new SecureRandom().nextBytes(bytes)
    passwordSalt = Base64.getEncoder.encodeToString(bytes).take(SaltBytes)

    // Hash password.
    passwordHash = hashPassword(passwordPlain, passwordSalt)
  }

  def checkPassword(passwordPlain: String): Boolean = {
    hashPassword(passwordPlain, passwordSalt) == passwordHash
  }

  private def hashPassword(passwordPlain: String, salt: String): String = {
    val spec = new PBEKeySpec(passwordPlain.toCharArray, salt.getBytes, HashIterations, HashBytes)
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    new String(factory.generateSecret(spec).getEncoded).take(HashBytes)
  }

}

object Admin {
  def create(email: String, password: String): Admin = {
    val admin = new Admin(-1, email, "", "")
    admin.setPassword(password)
    admin
  }
}