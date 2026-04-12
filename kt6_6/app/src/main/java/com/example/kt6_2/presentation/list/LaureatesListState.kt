package com.example.kt6_2.presentation.list

import com.example.kt6_2.domain.model.NobelPrize

sealed class LaureatesListState {
    data object Loading : LaureatesListState()
    data class Success(val prizes: List<NobelPrize>) : LaureatesListState()
    data class Error(val message: String) : LaureatesListState()
}