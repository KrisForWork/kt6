package com.example.kt6_2.presentation.favorites

import com.example.kt6_2.domain.model.NobelPrize

sealed class FavoritesState {
    data object Loading : FavoritesState()
    data class Success(val prizes: List<NobelPrize>) : FavoritesState()
    data class Error(val message: String) : FavoritesState()
}