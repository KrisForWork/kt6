package com.example.kt6_3.data.remote.api

import com.example.kt6_3.data.remote.dto.AuthResponse
import com.example.kt6_3.data.remote.dto.LoginRequest
import com.example.kt6_3.data.remote.dto.UserDto
import com.example.kt6_3.data.remote.dto.UsersResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class DummyJsonApi(private val client: HttpClient) {

    suspend fun login(request: LoginRequest): AuthResponse {
        return client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getAllUsers(): UsersResponse {
        return client.get("users") {
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun getUserById(id: Int): UserDto {
        return client.get("users/$id") {
            contentType(ContentType.Application.Json)
        }.body()
    }
}