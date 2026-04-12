package com.example.kt6_2.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Laureate(
    val id: String,
    val firstname: String,
    val surname: String? = null,
    val fullName: String? = null,
    val motivation: String = "",
    val portion: String? = null,
    val portraitUrl: String? = null
) {
    val displayName: String
        get() = fullName?.takeIf { it.isNotEmpty() }
            ?: buildString {
                append(firstname)
                surname?.let { append(" $it") }
            }
}