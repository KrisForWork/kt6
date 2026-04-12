package com.example.kt6_2.data.api.service

import com.example.kt6_2.data.api.models.NobelPrizeDto
import com.example.kt6_2.data.auth.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders

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
            val url = when {
                year != null && category != null -> {
                    android.util.Log.d("NobelApi", "Fetching specific prize: $year/$category")
                    val prize = fetchPrizeByYearAndCategory(year.toString(), category)
                    return if (prize != null) listOf(prize) else emptyList()
                }
                year != null -> {
                    android.util.Log.d("NobelApi", "Fetching prizes for year: $year")
                    "$baseUrl/prizes/$year"
                }
                category != null -> {
                    android.util.Log.d("NobelApi", "Fetching prizes for category: $category")
                    "$baseUrl/prizes/category/$category"
                }
                else -> {
                    // ✅ ИСПОЛЬЗУЕМ НОВЫЙ ЭНДПОИНТ /full
                    android.util.Log.d("NobelApi", "Fetching all prizes (FULL)")
                    "$baseUrl/prizes/full"
                }
            }

            android.util.Log.d("NobelApi", "========== REQUEST START ==========")
            android.util.Log.d("NobelApi", "URL: $url")
            android.util.Log.d("NobelApi", "Filters: year=$year, category=$category")

            val token = tokenManager.getToken()
            android.util.Log.d("NobelApi", "🔑 Token for request: ${token?.take(30)}...")

            val response = client.get(url) {
                token?.let {
                    headers.append(HttpHeaders.Authorization, "Bearer $it")
                    android.util.Log.d("NobelApi", "✅ Added Authorization header")
                }
            }

            android.util.Log.d("NobelApi", "Response status: ${response.status.value}")

            // ✅ ТЕПЕРЬ ПОЛУЧАЕМ СРАЗУ ПОЛНЫЕ ДАННЫЕ!
            val prizes = response.body<List<NobelPrizeDto>>()
            android.util.Log.d("NobelApi", "Got ${prizes.size} FULL prizes in ONE request!")

            prizes
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
            val response = client.get("$baseUrl/prizes/$year/$category") {
                token?.let {
                    headers.append(HttpHeaders.Authorization, "Bearer $it")
                }
            }

            response.body<NobelPrizeDto>()
        } catch (e: Exception) {
            android.util.Log.e("NobelApi", "Error fetching prize $year/$category: ${e.message}", e)
            null
        }
    }
}