package com.example.kt6_2.domain.model

data class FilterOptions(
    val year: Int? = null,
    val category: String? = null
)

object NobelCategories {
    val ALL = listOf(
        "physics",
        "chemistry",
        "medicine",
        "literature",
        "peace",
        "economics"
    )

    val DISPLAY_NAMES = mapOf(
        "physics" to "Physics",
        "chemistry" to "Chemistry",
        "medicine" to "Physiology or Medicine",
        "literature" to "Literature",
        "peace" to "Peace",
        "economics" to "Economic Sciences"
    )
}