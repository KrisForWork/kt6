package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Laureate(
    val id: String,
    val firstname: String,
    val surname: String? = null,
    val motivation: String,
    val share: String? = null
)