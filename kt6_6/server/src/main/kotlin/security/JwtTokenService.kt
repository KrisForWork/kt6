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
//        .withIssuer(JwtConfig.ISSUER)
        .withAudience(JwtConfig.AUDIENCE)
        .build()

    fun generateToken(username: String, role: String = "user", userId: Int): String {
        val now = Date()
        val expiryDate = Date(now.time + JwtConfig.VALIDITY_MS)

        return JWT.create()
            .withAudience(JwtConfig.AUDIENCE)
            .withSubject(username)
            .withIssuedAt(now)
            .withExpiresAt(expiryDate)
            .withClaim("username", username)
            .withClaim("role", role)
            .withClaim("userId", userId)
            .sign(algorithm)
    }

    fun verifyToken(token: String): DecodedJWT? {
        return try {
            verifier.verify(token)
        } catch (e: Exception) {
            println("JWT verification failed: ${e.message}")
            null
        }
    }

    fun getUsernameFromToken(token: String): String? {
        return verifyToken(token)?.subject
    }

    fun getUserIdFromToken(token: String): Int? {
        return verifyToken(token)?.getClaim("userId")?.asInt()
    }

    fun isTokenExpired(token: String): Boolean {
        val decoded = verifyToken(token) ?: return true
        return decoded.expiresAt.before(Date())
    }
}