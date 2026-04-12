package com.example.kt6_2.domain.repository

import com.example.kt6_2.domain.model.User

interface AuthRepository {

    suspend fun login(username: String, password: String): LoginResult

    suspend fun register(username: String, password: String): RegisterResult

    suspend fun logout()

    fun isLoggedIn(): Boolean

    fun getCurrentUser(): User?

    fun getToken(): String?

    sealed class LoginResult {
        data class Success(val user: User, val token: String) : LoginResult()
        data class Error(val message: String) : LoginResult()
        data object NetworkError : LoginResult()
    }

    sealed class RegisterResult {
        data class Success(val userId: Int, val username: String) : RegisterResult()
        data class Error(val message: String) : RegisterResult()
        data class UsernameTaken(val username: String) : RegisterResult()
        data object NetworkError : RegisterResult()
    }
}