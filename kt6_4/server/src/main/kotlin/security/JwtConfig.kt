package com.example.security

object JwtConfig {
    // Секретный ключ для подписи токена (минимум 32 символа)
    const val SECRET = "nobel-prize-api-secret-key-2024-very-long-32-chars"

    // Издатель токена
    const val ISSUER = "http://0.0.0.0:8080/"

    // Аудитория (для кого предназначен токен)
    const val AUDIENCE = "nobel-prize-api"

    // Время жизни токена - 30 минут в миллисекундах
    const val VALIDITY_MS = 30 * 60 * 1000L  // 30 минут

    // Realm для JWT (обычно используется для указания области действия)
    const val REALM = "ktor.io"
}