// app/src/main/java/com/example/kt6_2/data/repository/AuthRepositoryImpl.kt
package com.example.kt6_2.data.repository

import android.util.Log
import com.example.kt6_2.data.api.service.AuthApiService
import com.example.kt6_2.data.api.service.LoginResult as ApiLoginResult
import com.example.kt6_2.data.api.service.RegisterResult as ApiRegisterResult
import com.example.kt6_2.data.auth.TokenManager
import com.example.kt6_2.domain.model.User
import com.example.kt6_2.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(username: String, password: String): AuthRepository.LoginResult {
        android.util.Log.d("AuthRepo", "Login: $username")
        return withContext(Dispatchers.IO) {
            when (val result = authApiService.login(username, password)) {
                is ApiLoginResult.Success -> {
                    Log.d("AuthRepo", "FULL token from server: ${result.token}")
                    tokenManager.saveToken(result.token)
                    tokenManager.saveUserInfo(result.username, result.role)

                    val savedToken = tokenManager.getToken()
                    Log.d("AuthRepo", "FULL saved token check: $savedToken")

                    AuthRepository.LoginResult.Success(
                        user = User(username = result.username, role = result.role),
                        token = result.token
                    )
                }
                is ApiLoginResult.Error -> {
                    Log.e("AuthRepo", "Login error: ${result.message}")
                    AuthRepository.LoginResult.Error(result.message)
                }
                ApiLoginResult.NetworkError -> {
                    Log.e("AuthRepo", "Network error")
                    AuthRepository.LoginResult.NetworkError
                }
            }
        }
    }

    override suspend fun register(username: String, password: String): AuthRepository.RegisterResult {
        return withContext(Dispatchers.IO) {
            when (val result = authApiService.register(username, password)) {
                is ApiRegisterResult.Success -> AuthRepository.RegisterResult.Success(
                    userId = result.userId,
                    username = result.username
                )
                is ApiRegisterResult.Error -> AuthRepository.RegisterResult.Error(result.message)
                is ApiRegisterResult.UsernameTaken -> AuthRepository.RegisterResult.UsernameTaken(result.username)
                ApiRegisterResult.NetworkError -> AuthRepository.RegisterResult.NetworkError
            }
        }
    }

    override suspend fun logout() {
        tokenManager.clear()
    }

    override fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    override fun getCurrentUser(): User? {
        val username = tokenManager.getUsername() ?: return null
        val role = tokenManager.getRole() ?: "user"
        return User(username = username, role = role)
    }

    override fun getToken(): String? = tokenManager.getToken()
}