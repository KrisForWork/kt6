// app/src/main/java/com/example/kt6_2/domain/usecase/LoginUseCase.kt
package com.example.kt6_2.domain.usecase

import com.example.kt6_2.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        username: String,
        password: String
    ): LoginResult {
        return when (val result = authRepository.login(username, password)) {
            is AuthRepository.LoginResult.Success -> LoginResult.Success
            is AuthRepository.LoginResult.Error -> LoginResult.Error(result.message)
            AuthRepository.LoginResult.NetworkError -> LoginResult.NetworkError
        }
    }

    sealed class LoginResult {
        data object Success : LoginResult()
        data class Error(val message: String) : LoginResult()
        data object NetworkError : LoginResult()
    }
}