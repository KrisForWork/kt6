package com.example.models.dto

import kotlinx.serialization.Serializable

/**
 * Запрос на авторизацию
 */
@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

/**
 * Успешный ответ с токеном
 */
@Serializable
data class LoginResponse(
    val token: String,
    val username: String,
    val expiresIn: Long = 30 * 60
)

/**
 * Ответ с ошибкой
 */
@Serializable
data class ErrorResponse(
    val error: String,
    val message: String
)

/**
 * Health check ответ (опционально)
 */
@Serializable
data class HealthResponse(
    val status: String,
    val timestamp: Long
)