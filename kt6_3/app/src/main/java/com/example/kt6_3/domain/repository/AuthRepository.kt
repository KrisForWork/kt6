package com.example.kt6_3.domain.repository

import com.example.kt6_3.domain.model.AuthData

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<AuthData>
}