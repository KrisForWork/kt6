package com.example.plugins

import com.example.data.repository.ExposedPrizeRepository
import com.example.data.repository.ExposedUserRepository
import com.example.domain.usecase.*
import com.example.models.dto.*
import com.example.security.JwtTokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    val userRepository = ExposedUserRepository()
    val prizeRepository = ExposedPrizeRepository()
    val jwtTokenService = JwtTokenService()

    val registerUseCase = RegisterUseCase(userRepository)
    val loginUseCase = LoginUseCase(userRepository, jwtTokenService)
    val getUserProfileUseCase = GetUserProfileUseCase(userRepository)
    val getUserFavoritesUseCase = GetUserFavoritesUseCase(userRepository)
    val addFavoriteUseCase = AddFavoriteUseCase(userRepository)
    val removeFavoriteUseCase = RemoveFavoriteUseCase(userRepository)
    val getPrizesUseCase = GetPrizesUseCase(prizeRepository)
    val getPrizeByYearAndCategoryUseCase = GetPrizeByYearAndCategoryUseCase(prizeRepository)

    routing {
        get("/") {
            call.respondText(
                text = "Nobel Prize API Server",
                contentType = ContentType.Text.Plain
            )
        }

        get("/health") {
            call.respond(
                HealthResponse(
                    status = "OK",
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        route("/auth") {
            post("/register") {
                val request = call.receive<RegisterRequest>()

                if (request.username.length < 3) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(
                            error = "Validation Error",
                            message = "Username must be at least 3 characters"
                        )
                    )
                }

                if (request.password.length < 6 || request.password.length > 72) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(
                            error = "Validation Error",
                            message = "Password must be between 6 and 72 characters"
                        )
                    )
                }

                when (val result = registerUseCase(request.username, request.password)) {
                    is RegisterUseCase.Result.Success -> {
                        call.respond(
                            HttpStatusCode.Created,
                            RegisterResponse(
                                userId = result.userId,
                                username = request.username
                            )
                        )
                    }
                    is RegisterUseCase.Result.UsernameTaken -> {
                        call.respond(
                            HttpStatusCode.Conflict,
                            ErrorResponse(
                                error = "Username taken",
                                message = "Username '${request.username}' is already taken"
                            )
                        )
                    }
                }
            }

            post("/login") {
                val request = call.receive<LoginRequest>()

                when (val result = loginUseCase(request.username, request.password)) {
                    is LoginUseCase.LoginResult.Success -> {
                        call.respond(
                            HttpStatusCode.OK,
                            LoginResponse(
                                token = result.token,
                                username = result.username,
                                role = result.role
                            )
                        )
                    }
                    is LoginUseCase.LoginResult.InvalidCredentials -> {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            ErrorResponse(
                                error = "Invalid credentials",
                                message = "The username or password is incorrect"
                            )
                        )
                    }
                }
            }
        }

        authenticate("auth-jwt") {

            route("/users/me") {
                get {
                    val userId = call.getUserId() ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse("Unauthorized", "Invalid token")
                    )

                    val user = getUserProfileUseCase(userId)

                    if (user != null) {
                        call.respond(
                            HttpStatusCode.OK,
                            UserProfileResponse(
                                id = user.id!!,
                                username = user.username,
                                role = user.role,
                                createdAt = user.createdAt
                            )
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse("Not Found", "User not found")
                        )
                    }
                }

                get("/prizes") {
                    val userId = call.getUserId() ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse("Unauthorized", "Invalid token")
                    )

                    val favorites = getUserFavoritesUseCase(userId)
                    call.respond(HttpStatusCode.OK, favorites)
                }

                post("/prizes/{prizeId}") {
                    val userId = call.getUserId() ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse("Unauthorized", "Invalid token")
                    )

                    val prizeId = call.parameters["prizeId"]?.toIntOrNull()
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Bad Request", "Invalid prize ID")
                        )

                    val prize = prizeRepository.findPrizeById(prizeId)
                    if (prize == null) {
                        return@post call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse("Not Found", "Prize with ID $prizeId not found")
                        )
                    }

                    val success = addFavoriteUseCase(userId, prizeId)

                    call.respond(
                        HttpStatusCode.OK,
                        FavoriteResponse(
                            success = success,
                            message = if (success) "Prize added to favorites" else "Failed to add prize"
                        )
                    )
                }

                delete("/prizes/{prizeId}") {
                    val userId = call.getUserId() ?: return@delete call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse("Unauthorized", "Invalid token")
                    )

                    val prizeId = call.parameters["prizeId"]?.toIntOrNull()
                        ?: return@delete call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Bad Request", "Invalid prize ID")
                        )

                    val success = removeFavoriteUseCase(userId, prizeId)

                    call.respond(
                        HttpStatusCode.OK,
                        FavoriteResponse(
                            success = success,
                            message = if (success) "Prize removed from favorites" else "Prize was not in favorites"
                        )
                    )
                }
            }

            route("/prizes") {

                // ✅ НОВЫЙ ЭНДПОИНТ - возвращает полные данные
                get("/full") {
                    val prizes = prizeRepository.getAllPrizes()  // ← уже с лауреатами!
                    call.respond(HttpStatusCode.OK, prizes)
                }

                // Старый эндпоинт для обратной совместимости (summary)
                get {
                    val prizes = prizeRepository.getAllPrizes()
                    val summaries = prizes.map { it.toSummaryResponse() }
                    call.respond(HttpStatusCode.OK, summaries)
                }

                get("/{year}") {
                    val year = call.parameters["year"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Year required")
                    val prizes = prizeRepository.searchPrizes(year = year)
                    call.respond(HttpStatusCode.OK, prizes)
                }

                get("/category/{category}") {
                    val category = call.parameters["category"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Category required")
                    val prizes = prizeRepository.searchPrizes(category = category)
                    call.respond(HttpStatusCode.OK, prizes)
                }

                get("/{year}/{category}") {
                    val year = call.parameters["year"]
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Bad Request", "Year parameter is required")
                        )
                    val category = call.parameters["category"]
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse("Bad Request", "Category parameter is required")
                        )

                    val prize = prizeRepository.findPrizeByYearAndCategory(year, category)

                    if (prize != null) {
                        // ✅ ИСПРАВЛЕНО: проверяем что fullPrize не null
                        val fullPrize = prizeRepository.findPrizeById(prize.id!!)
                        if (fullPrize != null) {
                            call.respond(HttpStatusCode.OK, fullPrize)
                        } else {
                            call.respond(
                                HttpStatusCode.NotFound,
                                ErrorResponse(
                                    "Not Found",
                                    "Prize for year $year and category $category not found"
                                )
                            )
                        }
                    } else {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse(
                                "Not Found",
                                "Prize for year $year and category $category not found"
                            )
                        )
                    }
                }
            }
        }
    }
}

private fun ApplicationCall.getUserId(): Int? {
    val principal = principal<JWTPrincipal>() ?: return null
    val userId = principal.payload.getClaim("userId").asInt()
    return if (userId == 0) null else userId
}

private fun com.example.domain.model.NobelPrize.toSummaryResponse(): PrizeSummaryResponse {
    return PrizeSummaryResponse(
        awardYear = this.awardYear,
        category = this.category,
        prizeAmount = this.prizeAmount ?: 0L,
        laureatesCount = this.laureates.size
    )
}