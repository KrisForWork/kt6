package com.example.domain.usecase

import com.example.domain.model.Laureate
import com.example.domain.model.NobelPrize
import com.example.domain.repository.PrizeRepository

class GetPrizeByYearAndCategoryUseCase(
    private val prizeRepository: PrizeRepository
) {

    /**
     * Получить премию по году и категории
     * @param year год присуждения
     * @param category категория премии
     * @return NobelPrize или null, если не найдено
     */
    operator fun invoke(year: String, category: String): NobelPrize? {
        return prizeRepository.findPrize(year, category)
    }

    /**
     * Получить список лауреатов конкретной премии
     * @param year год присуждения
     * @param category категория премии
     * @return список лауреатов или пустой список
     */
    fun getLaureates(year: String, category: String): List<Laureate> {
        return prizeRepository.getLaureates(year, category)
    }
}