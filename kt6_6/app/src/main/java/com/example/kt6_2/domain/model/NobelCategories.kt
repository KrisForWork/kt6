// app/src/main/java/com/example/kt6_2/domain/model/NobelCategories.kt
package com.example.kt6_2.domain.model

object NobelCategories {
    val ALL = listOf("physics", "chemistry", "medicine", "literature", "peace", "economics")

    val DISPLAY_NAMES = mapOf(
        "physics" to "Physics",
        "chemistry" to "Chemistry",
        "medicine" to "Physiology or Medicine",
        "literature" to "Literature",
        "peace" to "Peace",
        "economics" to "Economic Sciences"
    )
}