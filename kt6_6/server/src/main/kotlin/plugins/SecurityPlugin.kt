package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.security.JwtConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = JwtConfig.REALM

            verifier(
                JWT
                    .require(Algorithm.HMAC256(JwtConfig.SECRET))
                    .withAudience(JwtConfig.AUDIENCE)
//                    .withIssuer(JwtConfig.ISSUER)
                    .build()
            )

            validate { credential ->
                if (credential.payload.audience.contains(JwtConfig.AUDIENCE)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

            challenge { _, _ ->
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = mapOf(
                        "error" to "Unauthorized",
                        "message" to "Token is not valid or has expired"
                    )
                )
            }
        }
    }
}

private fun isValidIssuerForDevelopment(issuer: String?): Boolean {
    if (issuer == null) return false

    val allowedIssuers = listOf(
        "http://0.0.0.0:8080/",
        "http://localhost:8080/",
        "http://10.0.2.2:8080/",
        "http://10.141.151.211:8080/",
        "http://127.0.0.1:8080/"
    )

    return issuer in allowedIssuers ||
            issuer.matches(Regex("http://(192\\.168\\..*|10\\..*|172\\.(1[6-9]|2[0-9]|3[0-1])\\..*):8080/"))
}