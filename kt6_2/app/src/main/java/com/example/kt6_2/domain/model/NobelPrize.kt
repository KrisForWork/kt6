package com.example.kt6_2.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NobelPrize(
    val year: String,
    val category: String,
    val categoryFullName: String? = null,
    val dateAwarded: String? = null,
    val motivation: String? = null,
    val prizeAmount: Int? = null,
    val prizeAmountAdjusted: Int? = null,
    val laureates: List<Laureate> = emptyList()
)