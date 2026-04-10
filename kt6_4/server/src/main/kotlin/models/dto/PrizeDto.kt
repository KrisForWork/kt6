package com.example.models.dto

import com.example.domain.model.Laureate
import com.example.domain.model.NobelPrize
import kotlinx.serialization.Serializable

/**
 * Ответ со списком лауреатов
 */
@Serializable
data class LaureatesResponse(
    val year: String,
    val category: String,
    val laureates: List<Laureate>
)

/**
 * Краткая информация о премии (для списка)
 */
@Serializable
data class PrizeSummaryResponse(
    val awardYear: String,
    val category: String,
    val prizeAmount: Long,
    val laureatesCount: Int
)

fun NobelPrize.toSummaryResponse(): PrizeSummaryResponse {
    return PrizeSummaryResponse(
        awardYear = this.awardYear,
        category = this.category.name,
        prizeAmount = this.prizeAmount,
        laureatesCount = this.laureates.size
    )
}