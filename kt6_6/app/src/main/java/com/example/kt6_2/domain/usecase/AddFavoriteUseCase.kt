// app/src/main/java/com/example/kt6_2/domain/usecase/AddFavoriteUseCase.kt
package com.example.kt6_2.domain.usecase

import com.example.kt6_2.domain.repository.FavoritesRepository
import com.example.kt6_2.utils.NetworkResult

class AddFavoriteUseCase(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(prizeId: Int): NetworkResult<Boolean> {
        return favoritesRepository.addFavorite(prizeId)
    }
}