package com.example.kt6_2.data.api.service

import com.example.kt6_2.data.api.models.NobelPrizeDto
import com.example.kt6_2.data.api.models.PrizeSummaryResponse
import com.example.kt6_2.data.auth.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json

interface NobelApiService {
    suspend fun fetchAllPrizes(
        year: Int? = null,
        category: String? = null
    ): List<NobelPrizeDto>

    suspend fun fetchPrizeByYearAndCategory(
        year: String,
        category: String
    ): NobelPrizeDto?
}

class NobelApiServiceImpl(
    private val client: HttpClient,
    private val tokenManager: TokenManager,
    private val baseUrl: String = "http://10.0.2.2:8080"
) : NobelApiService {

    override suspend fun fetchAllPrizes(year: Int?, category: String?): List<NobelPrizeDto> {
        return try {
            // Определяем URL в зависимости от фильтров
            val url = when {
                year != null && category != null -> {
                    // Если указаны оба - получаем конкретную премию
                    android.util.Log.d("NobelApi", "Fetching specific prize: $year/$category")
                    val prize = fetchPrizeByYearAndCategory(year.toString(), category)
                    return if (prize != null) listOf(prize) else emptyList()
                }
                year != null -> {
                    // Только год
                    android.util.Log.d("NobelApi", "Fetching prizes for year: $year")
                    "$baseUrl/prizes/$year"
                }
                category != null -> {
                    // Только категория
                    android.util.Log.d("NobelApi", "Fetching prizes for category: $category")
                    "$baseUrl/prizes/category/$category"
                }
                else -> {
                    // Без фильтров - все премии
                    android.util.Log.d("NobelApi", "Fetching all prizes")
                    "$baseUrl/prizes"
                }
            }

            android.util.Log.d("NobelApi", "========== REQUEST START ==========")
            android.util.Log.d("NobelApi", "URL: $url")
            android.util.Log.d("NobelApi", "Filters: year=$year, category=$category")

            // ✅ ЯВНО ДОБАВЛЯЕМ ТОКЕН
            val token = tokenManager.getToken()
            android.util.Log.d("NobelApi", "🔑 Token for request: ${token?.take(30)}...")

            val response = client.get(url) {
                token?.let {
                    headers.append(HttpHeaders.Authorization, "Bearer $it")
                    android.util.Log.d("NobelApi", "✅ Added Authorization header")
                } ?: run {
                    android.util.Log.d("NobelApi", "⚠️ No token available for request")
                }
            }

            android.util.Log.d("NobelApi", "Response status: ${response.status.value}")

            val responseBody = response.body<String>()
            android.util.Log.d("NobelApi", "Response: ${responseBody.take(200)}...")

            val summaries = Json.decodeFromString<List<PrizeSummaryResponse>>(responseBody)
            android.util.Log.d("NobelApi", "Got ${summaries.size} summaries")

            val result = summaries.mapNotNull { summary ->
                try {
                    fetchPrizeByYearAndCategory(summary.awardYear, summary.category)
                } catch (e: Exception) {
                    android.util.Log.e("NobelApi", "Failed details for ${summary.awardYear}/${summary.category}: ${e.message}")
                    null
                }
            }

            android.util.Log.d("NobelApi", "Returning ${result.size} full prizes")
            result
        } catch (e: Exception) {
            android.util.Log.e("NobelApi", "Error fetching prizes: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun fetchPrizeByYearAndCategory(
        year: String,
        category: String
    ): NobelPrizeDto? {
        return try {
            android.util.Log.d("NobelApi", "fetchPrizeByYearAndCategory: $year/$category")

            val token = tokenManager.getToken()
            android.util.Log.d("NobelApi", "🔑 Token for details: ${token?.take(30)}...")

            val response = client.get("$baseUrl/prizes/$year/$category") {
                token?.let {
                    headers.append(HttpHeaders.Authorization, "Bearer $it")
                    android.util.Log.d("NobelApi", "✅ Added Authorization header for details")
                } ?: run {
                    android.util.Log.d("NobelApi", "⚠️ No token available for details")
                }
            }

            val responseBody = response.body<String>()
            android.util.Log.d("NobelApi", "Details response: ${responseBody.take(200)}...")

            val body = response.body<NobelPrizeDto>()
            android.util.Log.d("NobelApi", "Got prize: ${body.awardYear} ${body.category} with ${body.laureates.size} laureates")
            body
        } catch (e: Exception) {
            android.util.Log.e("NobelApi", "Error fetching prize $year/$category: ${e.message}", e)
            null
        }
    }
}