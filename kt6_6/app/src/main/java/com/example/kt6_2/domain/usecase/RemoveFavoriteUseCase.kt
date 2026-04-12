// app/src/main/java/com/example/kt6_2/domain/usecase/RemoveFavoriteUseCase.kt
package com.example.kt6_2.domain.usecase

import com.example.kt6_2.domain.repository.FavoritesRepository
import com.example.kt6_2.utils.NetworkResult

class RemoveFavoriteUseCase(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(prizeId: Int): NetworkResult<Boolean> {
        return favoritesRepository.removeFavorite(prizeId)
    }
}