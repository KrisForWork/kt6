package com.example.kt6_2.data.repository

import com.example.kt6_2.data.api.service.NobelApiService
import com.example.kt6_2.data.mapper.toDomain
import com.example.kt6_2.data.mapper.toDomainList
import com.example.kt6_2.domain.model.Laureate
import com.example.kt6_2.domain.model.NobelPrize
import com.example.kt6_2.domain.repository.NobelRepository
import com.example.kt6_2.utils.NetworkResult
import com.example.kt6_2.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NobelRepositoryImpl(
    private val apiService: NobelApiService
) : NobelRepository {

    override suspend fun getNobelPrizes(
        year: Int?,
        category: String?
    ): NetworkResult<List<NobelPrize>> {
        return withContext(Dispatchers.IO) {
            safeApiCall {
                val prizes = apiService.fetchAllPrizes(
                    year = year,
                    category = category
                )
                prizes.toDomainList()
            }
        }
    }

    override suspend fun getPrizeDetails(
        year: String,
        category: String
    ): NetworkResult<NobelPrize> {
        return withContext(Dispatchers.IO) {
            safeApiCall {
                val prize = apiService.fetchPrizeByYearAndCategory(year, category)
                    ?: throw Exception("Prize not found")
                prize.toDomain()
            }
        }
    }
}