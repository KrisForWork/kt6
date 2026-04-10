package com.example.domain.usecase

import com.example.domain.repository.UserRepository

class AddFavoriteUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(userId: Int, prizeId: Int): Boolean {
        return userRepository.addFavorite(userId, prizeId)
    }
}