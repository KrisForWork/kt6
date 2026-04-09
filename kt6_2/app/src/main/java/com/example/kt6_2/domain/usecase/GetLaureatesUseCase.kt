package com.example.kt6_2.domain.usecase

import com.example.kt6_2.domain.repository.NobelRepository
import com.example.kt6_2.utils.NetworkResult

class GetLaureatesUseCase(
    private val repository: NobelRepository
) {

    suspend operator fun invoke(
        year: Int? = null,
        category: String? = null
    ): NetworkResult<List<com.example.kt6_2.domain.model.NobelPrize>> {
        return repository.getNobelPrizes(
            limit = 100,
            offset = 0,
            year = year,
            category = category
        )
    }
}