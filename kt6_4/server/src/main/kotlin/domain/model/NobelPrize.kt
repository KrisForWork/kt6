package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NobelPrize(
    val awardYear: String,
    val category: Category,
    val dateAwarded: String? = null,
    val prizeAmount: Long,
    val prizeAmountAdjusted: Long? = null,
    val laureates: List<Laureate> = emptyList()
)

@Serializable
enum class Category {
    physics,
    chemistry,
    medicine,
    literature,
    peace,
    economics;

    companion object {
        fun fromString(value: String): Category? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}