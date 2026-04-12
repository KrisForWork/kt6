// app/src/main/java/com/example/kt6_2/domain/usecase/LogoutUseCase.kt
package com.example.kt6_2.domain.usecase

import com.example.kt6_2.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke() {
        authRepository.logout()
    }
}