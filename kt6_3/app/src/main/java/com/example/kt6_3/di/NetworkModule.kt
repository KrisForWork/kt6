package com.example.kt6_3.di

import com.example.kt6_3.data.remote.api.DummyJsonApi
import com.example.kt6_3.data.remote.interceptor.AuthInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            explicitNulls = false
        }
    }

    single { AuthInterceptor(get()) }

    single {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(get<Json>())
            }

            install(Logging) {
                level = LogLevel.ALL
            }

            defaultRequest {
                url("https://dummyjson.com/")
                contentType(ContentType.Application.Json)
            }

            engine {
                endpoint {
                    connectTimeout = 30_000
                    socketTimeout = 30_000
                    requestTimeout = 30_000
                }
            }
        }

        get<AuthInterceptor>().configure(client)

        client
    }

    single { DummyJsonApi(get()) }
}