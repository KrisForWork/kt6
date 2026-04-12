package com.example.kt6_2.domain.repository

import com.example.kt6_2.domain.model.NobelPrize
import com.example.kt6_2.utils.NetworkResult

interface NobelRepository {

    suspend fun getNobelPrizes(
        year: Int? = null,
        category: String? = null
    ): NetworkResult<List<NobelPrize>>

    suspend fun getPrizeDetails(
        year: String,
        category: String
    ): NetworkResult<NobelPrize>
}