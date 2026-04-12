package com.example.kt6_2.data.api.exception

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import java.net.UnknownHostException
import java.net.SocketTimeoutException

fun Throwable.toUserMessage(): String {
    return when (this) {
        is UnknownHostException -> "No internet connection"
        is SocketTimeoutException -> "Connection timed out"
        is ClientRequestException -> "Client error: ${response.status.description}"
        is ServerResponseException -> "Server error: ${response.status.description}"
        is RedirectResponseException -> "Redirect error"
        else -> message ?: "Unknown error occurred"
    }
}