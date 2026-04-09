package com.example.kt6_2.domain.repository

import com.example.kt6_2.domain.model.NobelPrize
import com.example.kt6_2.utils.NetworkResult

interface NobelRepository {

    suspend fun getNobelPrizes(
        limit: Int = 25,
        offset: Int = 0,
        year: Int? = null,
        category: String? = null
    ): NetworkResult<List<NobelPrize>>
}