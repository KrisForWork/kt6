package com.example.kt6_2.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NobelPrize(
    val id: Int? = null,
    val awardYear: String,
    val category: String,
    val fullName: String? = null,
    val motivation: String? = null,
    val prizeAmount: Long? = null,
    val prizeAmountAdjusted: Long? = null,
    val dateAwarded: String? = null,
    val detailLink: String? = null,
    val laureates: List<Laureate> = emptyList()
)