package com.example.security

import at.favre.lib.crypto.bcrypt.BCrypt

/**
 * Утилита для хеширования и проверки паролей с использованием BCrypt
 */
object PasswordHasher {
    private const val BCRYPT_COST = 12

    /**
     * Хеширует пароль с использованием BCrypt
     * @param password пароль в открытом виде
     * @return хеш пароля
     */
    fun hash(password: String): String {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray())
    }

    /**
     * Проверяет соответствие пароля его хешу
     * @param password пароль в открытом виде
     * @param hash хеш пароля
     * @return true если пароль соответствует хешу
     */
    fun verify(password: String, hash: String): Boolean {
        return try {
            val result = BCrypt.verifyer().verify(password.toCharArray(), hash)
            result.verified
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Проверяет, нужно ли обновить хеш пароля
     * (например, если изменилась стоимость хеширования)
     * @param hash текущий хеш
     * @return true если хеш нужно обновить
     */
    fun needsRehash(hash: String): Boolean {
        return try {
            BCrypt.verifyer().verify("".toCharArray(), hash)
            false
        } catch (e: IllegalArgumentException) {
            // Если хеш поврежден или имеет неправильный формат
            true
        }
    }
}