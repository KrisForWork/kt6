// app/src/main/java/com/example/kt6_2/data/api/service/FavoritesApiService.kt
package com.example.kt6_2.data.api.service

import com.example.kt6_2.data.api.models.NobelPrizeDto
import com.example.kt6_2.data.api.models.PrizeSummaryResponse
import com.example.kt6_2.data.auth.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json

interface FavoritesApiService {
    suspend fun getFavorites(): List<PrizeSummaryResponse>
    suspend fun addFavorite(prizeId: Int): Boolean
    suspend fun removeFavorite(prizeId: Int): Boolean
    suspend fun getFullFavoritePrizes(): List<NobelPrizeDto>
}

class FavoritesApiServiceImpl(
    private val client: HttpClient,
    private val tokenManager: TokenManager,
    private val baseUrl: String = "http://10.0.2.2:8080"
) : FavoritesApiService {

    override suspend fun getFavorites(): List<PrizeSummaryResponse> {
        return try {
            android.util.Log.d("FavoritesApi", "Getting favorites...")

            val token = tokenManager.getToken()
            val response = client.get("$baseUrl/users/me/prizes") {
                token?.let {
                    headers.append(HttpHeaders.Authorization, "Bearer $it")
                }
            }

            val responseBody = response.body<String>()
            android.util.Log.d("FavoritesApi", "Favorites response: ${responseBody.take(200)}...")

            Json.decodeFromString<List<PrizeSummaryResponse>>(responseBody)
        } catch (e: Exception) {
            android.util.Log.e("FavoritesApi", "Error getting favorites: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun addFavorite(prizeId: Int): Boolean {
        return try {
            android.util.Log.d("FavoritesApi", "Adding favorite: $prizeId")

            val token = tokenManager.getToken()
            val response = client.post("$baseUrl/users/me/prizes/$prizeId") {
                token?.let {
                    headers.append(HttpHeaders.Authorization, "Bearer $it")
                }
            }

            response.status.value == 200
        } catch (e: Exception) {
            android.util.Log.e("FavoritesApi", "Error adding favorite: ${e.message}", e)
            false
        }
    }

    override suspend fun removeFavorite(prizeId: Int): Boolean {
        return try {
            android.util.Log.d("FavoritesApi", "Removing favorite: $prizeId")

            val token = tokenManager.getToken()
            val response = client.delete("$baseUrl/users/me/prizes/$prizeId") {
                token?.let {
                    headers.append(HttpHeaders.Authorization, "Bearer $it")
                }
            }

            response.status.value == 200
        } catch (e: Exception) {
            android.util.Log.e("FavoritesApi", "Error removing favorite: ${e.message}", e)
            false
        }
    }

    override suspend fun getFullFavoritePrizes(): List<NobelPrizeDto> {
        return try {
            val summaries = getFavorites()
            android.util.Log.d("FavoritesApi", "Got ${summaries.size} favorite summaries")

            // Получаем полные данные для каждой премии
            summaries.mapNotNull { summary ->
                try {
                    fetchPrizeDetails(summary.awardYear, summary.category)
                } catch (e: Exception) {
                    android.util.Log.e("FavoritesApi", "Failed to get details for ${summary.awardYear}/${summary.category}")
                    null
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("FavoritesApi", "Error getting full favorites: ${e.message}", e)
            emptyList()
        }
    }

    private suspend fun fetchPrizeDetails(year: String, category: String): NobelPrizeDto? {
        return try {
            val token = tokenManager.getToken()
            val response = client.get("$baseUrl/prizes/$year/$category") {
                token?.let {
                    headers.append(HttpHeaders.Authorization, "Bearer $it")
                }
            }
            response.body<NobelPrizeDto>()
        } catch (e: Exception) {
            null
        }
    }
}