package com.example.kt6_1.domain.extension

import com.example.kt6_1.presentation.ui.common.UiState

// Sealed class для UI состояний с правильными generic параметрами
sealed class DomainResult<out T> {
    data class Success<T>(val data: T) : DomainResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : DomainResult<Nothing>()
    data object Loading : DomainResult<Nothing>()
}

// Расширение для преобразования Result в DomainResult
fun <T> Result<T>.toDomainResult(): DomainResult<T> {
    return this.fold(
        onSuccess = { DomainResult.Success(it) },
        onFailure = { DomainResult.Error(it.message ?: "Неизвестная ошибка", it) }
    )
}

// Расширение для безопасного извлечения данных (исправлено имя функции)
fun <T> Result<T>.safeGetOrNull(): T? = this.getOrNull()

// Расширение для получения сообщения об ошибке
fun <T> Result<T>.getErrorMessage(): String {
    return this.exceptionOrNull()?.message ?: "Неизвестная ошибка"
}

// Дополнительное полезное расширение
fun <T> Result<T>.isSuccess(): Boolean = this.isSuccess

fun <T> Result<T>.isFailure(): Boolean = this.isFailure

// Для удобства работы в Compose
fun <T> DomainResult<T>.toUiState(): UiState<T> {
    return when (this) {
        is DomainResult.Success -> UiState.Success(data)
        is DomainResult.Error -> UiState.Error(message)
        DomainResult.Loading -> UiState.Loading
    }
}