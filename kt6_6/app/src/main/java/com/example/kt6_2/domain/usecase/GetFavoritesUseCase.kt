package com.example.kt6_2.domain.usecase

import com.example.kt6_2.domain.model.NobelPrize
import com.example.kt6_2.domain.repository.FavoritesRepository
import com.example.kt6_2.utils.NetworkResult

class GetFavoritesUseCase(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(): NetworkResult<List<NobelPrize>> {
        return favoritesRepository.getFavorites()
    }
}