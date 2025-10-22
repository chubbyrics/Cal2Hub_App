package com.example.cal2hubapp.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

object SecurityUtils {
    private const val SALT_LENGTH = 16
    
    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }
    
    fun hashPassword(rawPassword: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest((salt + rawPassword).toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
