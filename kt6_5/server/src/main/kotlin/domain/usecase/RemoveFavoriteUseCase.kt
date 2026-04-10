package com.example.domain.usecase

import com.example.domain.repository.UserRepository

class RemoveFavoriteUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(userId: Int, prizeId: Int): Boolean {
        return userRepository.removeFavorite(userId, prizeId)
    }
}