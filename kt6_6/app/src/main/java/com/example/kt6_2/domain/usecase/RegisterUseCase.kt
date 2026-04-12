// app/src/main/java/com/example/kt6_2/domain/usecase/RegisterUseCase.kt
package com.example.kt6_2.domain.usecase

import com.example.kt6_2.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        username: String,
        password: String
    ): RegisterResult {
        // Валидация на уровне use case
        if (username.length < 3) {
            return RegisterResult.ValidationError("Username must be at least 3 characters")
        }
        if (password.length < 6) {
            return RegisterResult.ValidationError("Password must be at least 6 characters")
        }

        return when (val result = authRepository.register(username, password)) {
            is AuthRepository.RegisterResult.Success -> RegisterResult.Success
            is AuthRepository.RegisterResult.Error -> RegisterResult.Error(result.message)
            is AuthRepository.RegisterResult.UsernameTaken -> RegisterResult.UsernameTaken
            AuthRepository.RegisterResult.NetworkError -> RegisterResult.NetworkError
        }
    }

    sealed class RegisterResult {
        data object Success : RegisterResult()
        data class Error(val message: String) : RegisterResult()
        data class ValidationError(val message: String) : RegisterResult()
        data object UsernameTaken : RegisterResult()
        data object NetworkError : RegisterResult()
    }
}