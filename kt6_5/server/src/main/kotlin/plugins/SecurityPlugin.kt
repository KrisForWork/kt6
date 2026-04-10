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
                    .withIssuer(JwtConfig.ISSUER)
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