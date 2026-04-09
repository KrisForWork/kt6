package com.example.kt6_2.navigation

import android.net.Uri
import com.example.kt6_2.domain.model.Laureate
import com.example.kt6_2.domain.model.NobelPrize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class DetailScreenData(
    val prize: NobelPrize,
    val laureate: Laureate
)

fun DetailScreenData.encodeToJson(): String {
    return Uri.encode(Json.encodeToString(this))
}

fun String.decodeFromJson(): DetailScreenData? {
    return try {
        val decoded = Uri.decode(this)
        Json.decodeFromString<DetailScreenData>(decoded)
    } catch (e: Exception) {
        null
    }
}