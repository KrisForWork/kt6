package com.example.security

object JwtConfig {
    const val SECRET = "nobel-prize-api-secret-key-2024-very-long-32-chars"

    const val ISSUER = "http://0.0.0.0:8080/"

    const val AUDIENCE = "nobel-prize-api"

    const val VALIDITY_MS = 30 * 60 * 1000L

    const val REALM = "ktor.io"
}