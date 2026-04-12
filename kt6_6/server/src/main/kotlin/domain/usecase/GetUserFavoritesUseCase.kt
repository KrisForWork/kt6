package com.example.domain.usecase

import com.example.domain.model.NobelPrize
import com.example.domain.repository.UserRepository

class GetUserFavoritesUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(userId: Int): List<NobelPrize> {
        return userRepository.getUserFavorites(userId)
    }
}