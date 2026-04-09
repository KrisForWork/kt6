package com.example.kt6_2.data.api.service

import com.example.kt6_2.data.api.models.NobelPrizesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class NobelApiService(
    private val httpClient: HttpClient
) {

    companion object {
        private const val BASE_URL = "https://api.nobelprize.org/2.1"
    }

    suspend fun fetchNobelPrizes(
        limit: Int = 25,
        offset: Int = 0,
        year: Int? = null,
        category: String? = null
    ): NobelPrizesResponse {
        return httpClient.get("$BASE_URL/nobelPrizes") {
            parameter("limit", limit)
            parameter("offset", offset)
            year?.let { parameter("nobelPrizeYear", it) }
            category?.let { parameter("nobelPrizeCategory", it) }
        }.body()
    }
}