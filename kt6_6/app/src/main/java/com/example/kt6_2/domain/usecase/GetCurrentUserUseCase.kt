// app/src/main/java/com/example/kt6_2/domain/usecase/GetCurrentUserUseCase.kt
package com.example.kt6_2.domain.usecase

import com.example.kt6_2.domain.model.User
import com.example.kt6_2.domain.repository.AuthRepository

class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {

    operator fun invoke(): User? = authRepository.getCurrentUser()

    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()
}