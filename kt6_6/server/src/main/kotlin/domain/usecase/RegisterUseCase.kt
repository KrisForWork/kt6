package com.example.domain.usecase

import com.example.domain.repository.UserRepository
import com.example.security.PasswordHasher

class RegisterUseCase(
    private val userRepository: UserRepository
) {

    sealed class Result {
        data class Success(val userId: Int) : Result()
        data object UsernameTaken : Result()
    }

    suspend operator fun invoke(username: String, password: String): Result {
        val existing = userRepository.findByUsername(username)
        if (existing != null) return Result.UsernameTaken

        val passwordHash = PasswordHasher.hash(password)
        val user = userRepository.createUser(username, passwordHash, "user")

        return if (user != null) Result.Success(user.id!!) else Result.UsernameTaken
    }
}