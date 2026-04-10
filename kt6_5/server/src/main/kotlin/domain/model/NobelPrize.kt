package com.example.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NobelPrize(
    val id: Int? = null,
    val awardYear: String,
    val category: String,
    val fullName: String? = null,
    val motivation: String? = null,
    val detailLink: String? = null,
    val prizeAmount: Long? = null,
    val prizeAmountAdjusted: Long? = null,
    val dateAwarded: String? = null,
    val laureates: List<Laureate> = emptyList()
)

@Serializable
enum class Category {
    physics, chemistry, medicine, literature, peace, economics;

    companion object {
        fun fromString(value: String): Category? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}