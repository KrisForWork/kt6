package com.example.security

import at.favre.lib.crypto.bcrypt.BCrypt

object PasswordHasher {
    private const val BCRYPT_COST = 12

    fun hash(password: String): String {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray())
    }

    fun verify(password: String, hash: String): Boolean {
        return try {
            val result = BCrypt.verifyer().verify(password.toCharArray(), hash)
            result.verified
        } catch (e: Exception) {
            false
        }
    }

    fun needsRehash(hash: String): Boolean {
        return try {
            BCrypt.verifyer().verify("".toCharArray(), hash)
            false
        } catch (e: IllegalArgumentException) {
            true
        }
    }
}