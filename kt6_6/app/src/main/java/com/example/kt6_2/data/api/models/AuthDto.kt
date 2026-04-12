package com.example.kt6_2.data.api.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val username: String,
    val role: String,
    val expiresIn: Long = 30 * 60
)

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String
)

@Serializable
data class RegisterResponse(
    val userId: Int,
    val username: String,
    val message: String = "User registered successfully"
)

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String
)

@Serializable
data class HealthResponse(
    val status: String,
    val timestamp: Long
)