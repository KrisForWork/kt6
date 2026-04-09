package com.example.kt6_2.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Laureate(
    val id: String,
    val fullName: String,
    val knownName: String? = null,
    val birthCountry: String? = null,
    val birthPlace: String? = null,
    val wikipediaUrl: String? = null
)