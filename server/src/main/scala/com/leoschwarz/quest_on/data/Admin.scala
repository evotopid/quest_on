package com.leoschwarz.quest_on.data

import java.security._
import java.util.Base64
import javax.crypto._
import javax.crypto.spec._

class Admin (val id: Int,
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
    factory.generateSecret(spec).getEncoded.toString
  }

}
