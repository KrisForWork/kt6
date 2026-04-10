package com.example.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.server.auth.jwt.*
import java.util.*

class JwtTokenService {

    private val algorithm: Algorithm = Algorithm.HMAC256(JwtConfig.SECRET)

    private val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(JwtConfig.ISSUER)
        .withAudience(JwtConfig.AUDIENCE)
        .build()

    /**
     * Генерирует JWT токен для пользователя
     * @param username имя пользователя
     * @return строка с JWT токеном
     */
    fun generateToken(username: String): String {
        val now = Date()
        val expiryDate = Date(now.time + JwtConfig.VALIDITY_MS)

        return JWT.create()
            .withIssuer(JwtConfig.ISSUER)
            .withAudience(JwtConfig.AUDIENCE)
            .withSubject(username)
            .withIssuedAt(now)
            .withExpiresAt(expiryDate)
            .withClaim("username", username)
            .sign(algorithm)
    }

    /**
     * Проверяет валидность JWT токена
     * @param token JWT токен в формате строки
     * @return DecodedJWT если токен валиден, иначе null
     */
    fun verifyToken(token: String): DecodedJWT? {
        return try {
            verifier.verify(token)
        } catch (e: Exception) {
            println("JWT verification failed: ${e.message}")
            null
        }
    }

    /**
     * Извлекает имя пользователя из валидного токена
     * @param token JWT токен
     * @return username или null
     */
    fun getUsernameFromToken(token: String): String? {
        return verifyToken(token)?.subject
    }

    /**
     * Проверяет, не истёк ли срок действия токена
     * @param token JWT токен
     * @return true если токен ещё действителен
     */
    fun isTokenExpired(token: String): Boolean {
        val decoded = verifyToken(token) ?: return true
        return decoded.expiresAt.before(Date())
    }
}

/**
 * Функция для создания JWTPrincipal из токена (используется в Ktor Authentication)
 */
fun jwtPrincipal(credential: JWTCredential): JWTPrincipal? {
    return try {
        JWTPrincipal(credential.payload)
    } catch (e: Exception) {
        null
    }
}