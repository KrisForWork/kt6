package com.example.domain.repository

import com.example.domain.model.User
import com.example.domain.model.NobelPrize

interface UserRepository {

    suspend fun findByUsername(username: String): User?

    suspend fun findById(id: Int): User?

    suspend fun createUser(username: String, passwordHash: String, role: String = "user"): User?

    suspend fun getUserFavorites(userId: Int): List<NobelPrize>

    suspend fun getFavoriteIds(userId: Int): List<Int>

    suspend fun addFavorite(userId: Int, prizeId: Int): Boolean

    suspend fun removeFavorite(userId: Int, prizeId: Int): Boolean

    suspend fun isFavorite(userId: Int, prizeId: Int): Boolean
}