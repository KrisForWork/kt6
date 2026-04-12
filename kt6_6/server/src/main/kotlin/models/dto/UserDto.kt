package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    val id: Int,
    val username: String,
    val role: String,
    val createdAt: String?
)

@Serializable
data class FavoriteResponse(
    val success: Boolean,
    val message: String
)