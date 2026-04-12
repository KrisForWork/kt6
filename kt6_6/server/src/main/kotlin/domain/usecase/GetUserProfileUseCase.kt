package com.example.domain.usecase

import com.example.domain.model.User
import com.example.domain.repository.UserRepository

class GetUserProfileUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(userId: Int): User? {
        return userRepository.findById(userId)
    }
}