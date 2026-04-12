package com.example.kt6_2.domain.repository

import com.example.kt6_2.domain.model.NobelPrize
import com.example.kt6_2.utils.NetworkResult

interface FavoritesRepository {
    suspend fun getFavorites(): NetworkResult<List<NobelPrize>>
    suspend fun addFavorite(prizeId: Int): NetworkResult<Boolean>
    suspend fun removeFavorite(prizeId: Int): NetworkResult<Boolean>
    suspend fun isFavorite(prizeId: Int): Boolean
}