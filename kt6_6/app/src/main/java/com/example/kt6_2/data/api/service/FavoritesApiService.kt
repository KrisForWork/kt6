// app/src/main/java/com/example/kt6_2/data/api/service/FavoritesApiService.kt
package com.example.kt6_2.data.api.service

import com.example.kt6_2.data.api.models.NobelPrizeDto
import com.example.kt6_2.data.auth.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders

interface FavoritesApiService {
    suspend fun getFavorites(): List<NobelPrizeDto>
    suspend fun addFavorite(prizeId: Int): Boolean
    suspend fun removeFavorite(prizeId: Int): Boolean
}

class FavoritesApiServiceImpl(
    private val client: HttpClient,
    private val tokenManager: TokenManager,
    private val baseUrl: String = "http://10.0.2.2:8080"
) : FavoritesApiService {

    override suspend fun getFavorites(): List<NobelPrizeDto> {
        return try {
            android.util.Log.d("FavoritesApi", "Request: GET /users/me/prizes")

            val token = tokenManager.getToken()
            val response = client.get("$baseUrl/users/me/prizes") {
                token?.let {
                    headers.append(HttpHeaders.Authorization, "Bearer $it")
                }
            }

            if (response.status.value == 200) {
                val prizes = response.body<List<NobelPrizeDto>>()
                android.util.Log.d("FavoritesApi", "Response: 200 OK - ${prizes.size} favorites")
                prizes
            } else {
                android.util.Log.e("FavoritesApi", "Response: ${response.status.value} ${response.status.description}")
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("FavoritesApi", "Error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun addFavorite(prizeId: Int): Boolean {
        return try {
            android.util.Log.d("FavoritesApi", "Request: POST /users/me/prizes/$prizeId")

            val token = tokenManager.getToken()
            val response = client.post("$baseUrl/users/me/prizes/$prizeId") {
                token?.let {
                    headers.append(HttpHeaders.Authorization, "Bearer $it")
                }
            }

            val success = response.status.value == 200
            if (success) {
                android.util.Log.d("FavoritesApi", "Response: 200 OK")
            } else {
                android.util.Log.e("FavoritesApi", "Response: ${response.status.value} ${response.status.description}")
            }
            success
        } catch (e: Exception) {
            android.util.Log.e("FavoritesApi", "Error: ${e.message}")
            false
        }
    }

    override suspend fun removeFavorite(prizeId: Int): Boolean {
        return try {
            android.util.Log.d("FavoritesApi", "Request: DELETE /users/me/prizes/$prizeId")

            val token = tokenManager.getToken()
            val response = client.delete("$baseUrl/users/me/prizes/$prizeId") {
                token?.let {
                    headers.append(HttpHeaders.Authorization, "Bearer $it")
                }
            }

            val success = response.status.value == 200
            if (success) {
                android.util.Log.d("FavoritesApi", "Response: 200 OK")
            } else {
                android.util.Log.e("FavoritesApi", "Response: ${response.status.value} ${response.status.description}")
            }
            success
        } catch (e: Exception) {
            android.util.Log.e("FavoritesApi", "Error: ${e.message}")
            false
        }
    }
}