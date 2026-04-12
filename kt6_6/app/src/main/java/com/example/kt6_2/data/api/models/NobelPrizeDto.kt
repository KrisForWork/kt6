package com.example.kt6_2.data.api.models

import kotlinx.serialization.Serializable

@Serializable
data class NobelPrizesResponse(
    val prizes: List<NobelPrizeDto> = emptyList()
)

@Serializable
data class NobelPrizeDto(
    val id: Int? = null,
    val awardYear: String? = "",
    val category: String? = "",
    val fullName: String? = null,
    val motivation: String? = null,
    val prizeAmount: Long? = null,
    val prizeAmountAdjusted: Long? = null,
    val dateAwarded: String? = null,
    val detailLink: String? = null,
    val laureates: List<LaureateDto> = emptyList()
)

@Serializable
data class LaureateDto(
    val id: String,
    val firstname: String = "",
    val surname: String? = null,
    val fullName: String? = null,
    val motivation: String = "",
    val portion: String? = null,
    val portraitUrl: String? = null
)

@Serializable
data class PrizeSummaryResponse(
    val awardYear: String,
    val category: String,
    val prizeAmount: Long,
    val laureatesCount: Int
)