package com.example.kt6_3.domain.model

import com.example.kt6_3.data.remote.interceptor.UnauthorizedException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class AppError : Exception() {
    object NetworkError : AppError()
    object Unauthorized : AppError()
    object NotFound : AppError()
    data class ValidationError(override val message: String) : AppError()
    data class ServerError(val code: Int, override val message: String) : AppError()
    data class UnknownError(override val message: String) : AppError()
}

fun Throwable.toAppError(): AppError {
    return when (this) {
        is UnknownHostException,
        is SocketTimeoutException,
        is java.io.IOException -> AppError.NetworkError

        is UnauthorizedException -> AppError.Unauthorized

        is ClientRequestException -> {
            when (response.status.value) {
                400 -> AppError.ValidationError("Неверный логин или пароль")
                401 -> AppError.Unauthorized
                404 -> AppError.NotFound
                else -> AppError.ServerError(response.status.value, "Ошибка клиента: ${response.status.value}")
            }
        }

        is ServerResponseException -> {
            AppError.ServerError(response.status.value, "Ошибка сервера: ${response.status.value}")
        }

        else -> AppError.UnknownError(message ?: "Неизвестная ошибка: ${this::class.simpleName}")
    }
}

fun AppError.getUserMessage(): String {
    return when (this) {
        is AppError.NetworkError -> "Отсутствует подключение к интернету"
        is AppError.Unauthorized -> "Сессия истекла. Пожалуйста, войдите снова"
        is AppError.NotFound -> "Данные не найдены"
        is AppError.ValidationError -> message
        is AppError.ServerError -> "Ошибка сервера (код $code): $message"
        is AppError.UnknownError -> message
    }
}