package com.example.kt6_2.di

import android.content.Context
import android.util.Log
import com.example.kt6_2.data.api.service.AuthApiService
import com.example.kt6_2.data.api.service.AuthApiServiceImpl
import com.example.kt6_2.data.api.service.NobelApiService
import com.example.kt6_2.data.api.service.NobelApiServiceImpl
import com.example.kt6_2.data.auth.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object NetworkModule {

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        explicitNulls = false
    }

    fun provideHttpClient(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(jsonConfig)
            }

            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("Ktor", message)
                    }
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 15000
                socketTimeoutMillis = 30000
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
                exponentialDelay()
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
                // Токен будет добавляться в каждом сервисе явно
            }
        }
    }

    fun provideNobelApiService(client: HttpClient, tokenManager: TokenManager): NobelApiService {
        return NobelApiServiceImpl(client, tokenManager)
    }

    fun provideAuthApiService(client: HttpClient): AuthApiService {
        return AuthApiServiceImpl(client)
    }

    fun provideTokenManager(context: Context): TokenManager {
        return TokenManager(context)
    }
}