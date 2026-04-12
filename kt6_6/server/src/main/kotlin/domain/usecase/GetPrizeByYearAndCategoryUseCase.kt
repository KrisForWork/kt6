package com.example.domain.usecase

import com.example.domain.model.Laureate
import com.example.domain.model.NobelPrize
import com.example.domain.repository.PrizeRepository

class GetPrizeByYearAndCategoryUseCase(
    private val prizeRepository: PrizeRepository
) {

    suspend operator fun invoke(year: String, category: String): NobelPrize? {
        return prizeRepository.findPrizeByYearAndCategory(year, category)
    }

    suspend fun getLaureates(year: String, category: String): List<Laureate> {
        return prizeRepository.getLaureatesByYearAndCategory(year, category)
    }
}