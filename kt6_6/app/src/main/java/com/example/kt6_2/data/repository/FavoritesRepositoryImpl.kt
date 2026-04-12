// app/src/main/java/com/example/kt6_2/data/repository/FavoritesRepositoryImpl.kt
package com.example.kt6_2.data.repository

import com.example.kt6_2.data.api.service.FavoritesApiService
import com.example.kt6_2.data.mapper.toDomain
import com.example.kt6_2.domain.model.NobelPrize
import com.example.kt6_2.domain.repository.FavoritesRepository
import com.example.kt6_2.utils.NetworkResult
import com.example.kt6_2.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class FavoritesRepositoryImpl(
    private val apiService: FavoritesApiService
) : FavoritesRepository {

    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    override suspend fun getFavorites(): NetworkResult<List<NobelPrize>> {
        return withContext(Dispatchers.IO) {
            safeApiCall {
                val prizes = apiService.getFullFavoritePrizes()
                val domainPrizes = prizes.map { it.toDomain() }

                // Обновляем кэш ID избранных
                _favoriteIds.value = domainPrizes.mapNotNull { it.id }.toSet()

                domainPrizes
            }
        }
    }

    override suspend fun addFavorite(prizeId: Int): NetworkResult<Boolean> {
        return withContext(Dispatchers.IO) {
            safeApiCall {
                val success = apiService.addFavorite(prizeId)
                if (success) {
                    _favoriteIds.value = _favoriteIds.value + prizeId
                }
                success
            }
        }
    }

    override suspend fun removeFavorite(prizeId: Int): NetworkResult<Boolean> {
        return withContext(Dispatchers.IO) {
            safeApiCall {
                val success = apiService.removeFavorite(prizeId)
                if (success) {
                    _favoriteIds.value = _favoriteIds.value - prizeId
                }
                success
            }
        }
    }

    override suspend fun isFavorite(prizeId: Int): Boolean {
        return prizeId in _favoriteIds.value
    }

    // Для инициализации кэша при старте
    suspend fun loadFavoriteIds() {
        try {
            val summaries = apiService.getFavorites()
            // У нас нет ID в summary, нужно получить полные данные
            val prizes = apiService.getFullFavoritePrizes()
            _favoriteIds.value = prizes.mapNotNull { it.id }.toSet()
        } catch (e: Exception) {
            android.util.Log.e("FavoritesRepo", "Error loading favorite IDs: ${e.message}")
        }
    }
}