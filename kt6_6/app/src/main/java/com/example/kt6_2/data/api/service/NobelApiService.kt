package com.example.kt6_2.data.api.service

import com.example.kt6_2.data.api.models.NobelPrizeDto
import com.example.kt6_2.data.auth.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
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
                    android.util.Log.d("NobelApi", "Request: GET /prizes/$year/$category")
                    val prize = fetchPrizeByYearAndCategory(year.toString(), category)
                    return if (prize != null) listOf(prize) else emptyList()
                }
                year != null -> {
                    android.util.Log.d("NobelApi", "Request: GET /prizes/$year")
                    "$baseUrl/prizes/$year"
                }
                category != null -> {
                    android.util.Log.d("NobelApi", "Request: GET /prizes/category/$category")
                    "$baseUrl/prizes/category/$category"
                }
                else -> {
                    android.util.Log.d("NobelApi", "Request: GET /prizes/full")
                    "$baseUrl/prizes/full"
                }
            }

            val token = tokenManager.getToken()
            val response = client.get(url) {
                token?.let {
                    headers.append(HttpHeaders.Authorization, "Bearer $it")
                }
            }

            if (response.status.value == 200) {
                val prizes = response.body<List<NobelPrizeDto>>()
                android.util.Log.d("NobelApi", "Response: 200 OK - ${prizes.size} prizes")
                prizes
            } else {
                android.util.Log.e("NobelApi", "Response: ${response.status.value} ${response.status.description}")
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("NobelApi", "Error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun fetchPrizeByYearAndCategory(
        year: String,
        category: String
    ): NobelPrizeDto? {
        return try {
            val token = tokenManager.getToken()
            val response = client.get("$baseUrl/prizes/$year/$category") {
                token?.let {
                    headers.append(HttpHeaders.Authorization, "Bearer $it")
                }
            }

            if (response.status.value == 200) {
                response.body<NobelPrizeDto>()
            } else {
                android.util.Log.e("NobelApi", "Response: ${response.status.value} ${response.status.description}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("NobelApi", "Error: ${e.message}")
            null
        }
    }
}