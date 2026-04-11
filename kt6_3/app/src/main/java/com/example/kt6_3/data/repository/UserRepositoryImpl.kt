package com.example.kt6_3.data.repository

import com.example.kt6_3.data.local.TokenStorage
import com.example.kt6_3.data.remote.api.DummyJsonApi
import com.example.kt6_3.data.remote.dto.UserDto
import com.example.kt6_3.data.remote.interceptor.UnauthorizedException
import com.example.kt6_3.domain.model.User
import com.example.kt6_3.domain.repository.UserRepository

class UserRepositoryImpl(
    private val api: DummyJsonApi,
    private val tokenStorage: TokenStorage
) : UserRepository {

    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val response = api.getAllUsers()
            Result.success(response.users.map { it.toDomain() })
        } catch (e: UnauthorizedException) {
            tokenStorage.clearToken()
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserById(id: Int): Result<User> {
        return try {
            val dto = api.getUserById(id)
            Result.success(dto.toDomain())
        } catch (e: UnauthorizedException) {
            tokenStorage.clearToken()
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun UserDto.toDomain(): User {
        return User(
            id = id,
            firstName = firstName,
            lastName = lastName,
            username = username,
            email = email,
            image = image
        )
    }
}