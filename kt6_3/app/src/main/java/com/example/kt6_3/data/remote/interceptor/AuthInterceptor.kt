package com.example.kt6_3.data.remote.interceptor

import com.example.kt6_3.data.local.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking

class AuthInterceptor(private val tokenStorage: TokenStorage) {

    fun configure(client: HttpClient) {
        client.sendPipeline.intercept(HttpSendPipeline.State) {
            runBlocking {
                tokenStorage.getToken()?.let { token ->
                    if (token.isNotBlank()) {
                        context.headers.append(
                            HttpHeaders.Authorization,
                            "Bearer $token"
                        )
                    }
                }
            }
        }

        client.receivePipeline.intercept(HttpReceivePipeline.State) { response ->
            if (response.status == HttpStatusCode.Unauthorized) {
                runBlocking {
                    tokenStorage.clearToken()
                }
                throw UnauthorizedException("Token expired or invalid")
            }
            proceedWith(response)
        }
    }
}

class UnauthorizedException(message: String) : Exception(message)