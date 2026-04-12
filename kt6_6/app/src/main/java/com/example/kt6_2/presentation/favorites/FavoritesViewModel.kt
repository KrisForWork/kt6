// app/src/main/java/com/example/kt6_2/presentation/favorites/FavoritesViewModel.kt
package com.example.kt6_2.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kt6_2.domain.usecase.AddFavoriteUseCase
import com.example.kt6_2.domain.usecase.GetFavoritesUseCase
import com.example.kt6_2.domain.usecase.RemoveFavoriteUseCase
import com.example.kt6_2.utils.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<FavoritesState>(FavoritesState.Loading)
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    private val _isFavoriteMap = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val isFavoriteMap: StateFlow<Map<Int, Boolean>> = _isFavoriteMap.asStateFlow()

    private val _favoriteOperationInProgress = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteOperationInProgress: StateFlow<Set<Int>> = _favoriteOperationInProgress.asStateFlow()

    fun loadFavorites() {
        viewModelScope.launch {
            _state.value = FavoritesState.Loading

            when (val result = getFavoritesUseCase()) {
                is NetworkResult.Success -> {
                    _state.value = FavoritesState.Success(result.data)
                    // Обновляем карту избранных
                    val map = result.data.associate { prize ->
                        (prize.id ?: 0) to true
                    }
                    _isFavoriteMap.value = map
                }
                is NetworkResult.Error -> {
                    _state.value = FavoritesState.Error(result.message)
                }
                NetworkResult.Loading -> {
                    _state.value = FavoritesState.Loading
                }
            }
        }
    }

    fun addFavorite(prizeId: Int) {
        viewModelScope.launch {
            _favoriteOperationInProgress.value = _favoriteOperationInProgress.value + prizeId

            when (addFavoriteUseCase(prizeId)) {
                is NetworkResult.Success -> {
                    _isFavoriteMap.value = _isFavoriteMap.value + (prizeId to true)
                }
                else -> {
                    // Ошибка - можно показать уведомление
                }
            }

            _favoriteOperationInProgress.value = _favoriteOperationInProgress.value - prizeId
        }
    }

    fun removeFavorite(prizeId: Int) {
        viewModelScope.launch {
            _favoriteOperationInProgress.value = _favoriteOperationInProgress.value + prizeId

            when (removeFavoriteUseCase(prizeId)) {
                is NetworkResult.Success -> {
                    _isFavoriteMap.value = _isFavoriteMap.value - prizeId
                    // Если на экране избранного - обновляем список
                    val currentState = _state.value
                    if (currentState is FavoritesState.Success) {
                        val updatedPrizes = currentState.prizes.filter { it.id != prizeId }
                        _state.value = FavoritesState.Success(updatedPrizes)
                    }
                }
                else -> {
                    // Ошибка
                }
            }

            _favoriteOperationInProgress.value = _favoriteOperationInProgress.value - prizeId
        }
    }

    fun isFavorite(prizeId: Int): Boolean {
        return _isFavoriteMap.value[prizeId] == true
    }

    fun refresh() {
        loadFavorites()
    }
}