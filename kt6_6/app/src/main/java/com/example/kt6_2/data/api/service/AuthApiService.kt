package com.example.kt6_2.data.api.service

import com.example.kt6_2.data.api.models.LoginRequest
import com.example.kt6_2.data.api.models.LoginResponse
import com.example.kt6_2.data.api.models.RegisterRequest
import com.example.kt6_2.data.api.models.RegisterResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface AuthApiService {
    suspend fun login(username: String, password: String): LoginResult
    suspend fun register(username: String, password: String): RegisterResult
}

sealed class LoginResult {
    data class Success(val token: String, val username: String, val role: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
    data object NetworkError : LoginResult()
}

sealed class RegisterResult {
    data class Success(val userId: Int, val username: String) : RegisterResult()
    data class Error(val message: String) : RegisterResult()
    data class UsernameTaken(val username: String) : RegisterResult()
    data object NetworkError : RegisterResult()
}

class AuthApiServiceImpl(
    private val client: HttpClient,
    private val baseUrl: String = "http://10.0.2.2:8080"
) : AuthApiService {

    override suspend fun login(username: String, password: String): LoginResult {
        return try {
            val response = client.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(username, password))
            }

            when (response.status.value) {
                200 -> {
                    val loginResponse = response.body<LoginResponse>()
                    LoginResult.Success(
                        token = loginResponse.token,
                        username = loginResponse.username,
                        role = loginResponse.role
                    )
                }
                401 -> LoginResult.Error("Invalid username or password")
                else -> LoginResult.Error("Login failed: ${response.status.description}")
            }
        } catch (e: Exception) {
            println("Login error: ${e.message}")
            LoginResult.NetworkError
        }
    }

    override suspend fun register(username: String, password: String): RegisterResult {
        return try {
            val response = client.post("$baseUrl/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(username, password))
            }

            when (response.status.value) {
                201 -> {
                    val registerResponse = response.body<RegisterResponse>()
                    RegisterResult.Success(
                        userId = registerResponse.userId,
                        username = registerResponse.username
                    )
                }
                409 -> RegisterResult.UsernameTaken(username)
                400 -> {
                    val error = response.body<com.example.kt6_2.data.api.models.ErrorResponse>()
                    RegisterResult.Error(error.message)
                }
                else -> RegisterResult.Error("Registration failed: ${response.status.description}")
            }
        } catch (e: Exception) {
            println("Register error: ${e.message}")
            RegisterResult.NetworkError
        }
    }
}