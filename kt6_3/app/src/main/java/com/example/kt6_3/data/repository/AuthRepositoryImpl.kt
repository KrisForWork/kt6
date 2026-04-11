package com.example.kt6_3.data.repository

import android.util.Log
import com.example.kt6_3.data.local.TokenStorage
import com.example.kt6_3.data.remote.api.DummyJsonApi
import com.example.kt6_3.data.remote.dto.LoginRequest
import com.example.kt6_3.data.remote.interceptor.UnauthorizedException
import com.example.kt6_3.domain.model.AuthData
import com.example.kt6_3.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: DummyJsonApi,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    companion object {
        private const val TAG = "AuthRepository"
    }

    override suspend fun login(username: String, password: String): Result<AuthData> {
        return try {
            Log.d(TAG, "Attempting login for user: $username")
            val response = api.login(LoginRequest(username, password))
            Log.d(TAG, "Login successful, saving token")
            tokenStorage.saveToken(response.token)
            Result.success(response.toDomain())
        } catch (e: UnauthorizedException) {
            Log.e(TAG, "Unauthorized", e)
            tokenStorage.clearToken()
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Login failed", e)
            Result.failure(e)
        }
    }

    private fun com.example.kt6_3.data.remote.dto.AuthResponse.toDomain(): AuthData {
        return AuthData(
            id = id,
            username = username,
            email = email,
            firstName = firstName,
            lastName = lastName,
            image = image,
            token = token
        )
    }
}