package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int? = null,
    val username: String,
    val passwordHash: String,
    val role: String = "user",
    val createdAt: String? = null
)

@Serializable
enum class UserRole {
    USER, ADMIN;

    companion object {
        fun fromString(value: String): UserRole {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: USER
        }
    }
}