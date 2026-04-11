package com.example.kt6_3.presentation.common

import com.example.kt6_3.domain.model.AppError

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val error: AppError) : UiState<Nothing>()
}