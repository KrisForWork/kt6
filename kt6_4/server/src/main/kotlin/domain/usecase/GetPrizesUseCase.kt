package com.example.domain.usecase

import com.example.domain.model.NobelPrize
import com.example.domain.repository.PrizeRepository

class GetPrizesUseCase(
    private val prizeRepository: PrizeRepository
) {

    /**
     * Получить список всех Нобелевских премий
     * @return список всех премий
     */
    operator fun invoke(): List<NobelPrize> {
        return prizeRepository.getAllPrizes()
    }
}