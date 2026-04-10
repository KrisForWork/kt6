package com.example.domain.usecase

import com.example.security.JwtTokenService

class LoginUseCase(
    private val jwtTokenService: JwtTokenService
) {

    // Простой словарь пользователей (в реальном приложении - база данных)
    private val users = mapOf(
        "admin" to "admin123",
        "user" to "user123",
        "nobel" to "prize2024"
    )

    /**
     * Результат попытки входа
     */
    sealed class LoginResult {
        data class Success(val token: String) : LoginResult()
        data object InvalidCredentials : LoginResult()
        data object UserNotFound : LoginResult()
    }

    /**
     * Выполнить вход в систему
     * @param username имя пользователя
     * @param password пароль
     * @return LoginResult с токеном или ошибкой
     */
    operator fun invoke(username: String, password: String): LoginResult {
        // Проверяем, существует ли пользователь
        val storedPassword = users[username]

        return if (storedPassword == null) {
            LoginResult.UserNotFound
        } else if (storedPassword != password) {
            LoginResult.InvalidCredentials
        } else {
            // Генерируем JWT токен
            val token = jwtTokenService.generateToken(username)
            LoginResult.Success(token)
        }
    }

    /**
     * Проверить валидность токена
     * @param token JWT токен
     * @return true если токен валиден
     */
    fun validateToken(token: String): Boolean {
        return jwtTokenService.verifyToken(token) != null
    }

    /**
     * Получить имя пользователя из токена
     * @param token JWT токен
     * @return username или null
     */
    fun getUsernameFromToken(token: String): String? {
        return jwtTokenService.getUsernameFromToken(token)
    }
}