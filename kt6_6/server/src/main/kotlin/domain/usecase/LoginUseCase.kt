package com.example.domain.usecase

import com.example.domain.repository.UserRepository
import com.example.security.JwtTokenService
import com.example.security.PasswordHasher

class LoginUseCase(
    private val userRepository: UserRepository,
    private val jwtTokenService: JwtTokenService
) {

    sealed class LoginResult {
        data class Success(val token: String, val username: String, val role: String) : LoginResult()
        data object InvalidCredentials : LoginResult()
    }

    suspend operator fun invoke(username: String, password: String): LoginResult {
        val user = userRepository.findByUsername(username) ?: return LoginResult.InvalidCredentials

        return if (PasswordHasher.verify(password, user.passwordHash)) {
            LoginResult.Success(
                token = jwtTokenService.generateToken(
                    username = username,
                    role = user.role,
                    userId = user.id ?: 0
                ),
                username = username,
                role = user.role
            )
        } else {
            LoginResult.InvalidCredentials
        }
    }
}