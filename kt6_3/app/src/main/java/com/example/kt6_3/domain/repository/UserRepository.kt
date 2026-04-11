package com.example.kt6_3.domain.repository

import com.example.kt6_3.domain.model.User

interface UserRepository {
    suspend fun getAllUsers(): Result<List<User>>
    suspend fun getUserById(id: Int): Result<User>
}