package com.example.models.dto

import com.example.domain.model.Laureate
import kotlinx.serialization.Serializable

@Serializable
data class PrizeSummaryResponse(
    val awardYear: String,
    val category: String,
    val prizeAmount: Long,
    val laureatesCount: Int
)

@Serializable
data class LaureatesResponse(
    val year: String,
    val category: String,
    val laureates: List<Laureate>
)