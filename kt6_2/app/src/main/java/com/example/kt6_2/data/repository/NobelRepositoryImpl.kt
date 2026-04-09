package com.example.kt6_2.data.repository

import com.example.kt6_2.data.api.service.NobelApiService
import com.example.kt6_2.data.mapper.toDomainList
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
        limit: Int,
        offset: Int,
        year: Int?,
        category: String?
    ): NetworkResult<List<NobelPrize>> {
        return withContext(Dispatchers.IO) {
            val result = safeApiCall {
                apiService.fetchNobelPrizes(
                    limit = limit,
                    offset = offset,
                    year = year,
                    category = category
                )
            }

            when (result) {
                is NetworkResult.Success -> {
                    val domainList = result.data.nobelPrizes.toDomainList()
                    NetworkResult.Success(domainList)
                }
                is NetworkResult.Error -> result
                NetworkResult.Loading -> NetworkResult.Loading
            }
        }
    }
}