package com.example.plugins

import com.example.data.repository.ExposedPrizeRepository
import com.example.data.repository.ExposedUserRepository
import com.example.domain.usecase.*
import com.example.models.dto.*
import com.example.security.JwtTokenService
import com.example.security.PasswordHasher
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    // ============ ИНИЦИАЛИЗАЦИЯ РЕПОЗИТОРИЕВ ============
    val userRepository = ExposedUserRepository()
    val prizeRepository = ExposedPrizeRepository()
    val jwtTokenService = JwtTokenService()

    // ============ USE CASES ============
    val registerUseCase = RegisterUseCase(userRepository)
    val loginUseCase = LoginUseCase(userRepository, jwtTokenService)
    val getUserProfileUseCase = GetUserProfileUseCase(userRepository)
    val getUserFavoritesUseCase = GetUserFavoritesUseCase(userRepository)
    val addFavoriteUseCase = AddFavoriteUseCase(userRepository)
    val removeFavoriteUseCase = RemoveFavoriteUseCase(userRepository)
    val getPrizesUseCase = GetPrizesUseCase(prizeRepository)
    val getPrizeByYearAndCategoryUseCase = GetPrizeByYearAndCategoryUseCase(prizeRepository)

    routing {

        // ============ ПУБЛИЧНЫЕ ЭНДПОИНТЫ ============

        // Корневой эндпоинт
        get("/") {
            call.respondText(
                text = "Nobel Prize API Server",
                contentType = ContentType.Text.Plain
            )
        }

        // Health check
        get("/health") {
            call.respond(
                HealthResponse(
                    status = "OK",
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        // ============ АВТОРИЗАЦИЯ ============
        route("/auth") {

            // POST /auth/register - регистрация
            post("/register") {
                val request = call.receive<RegisterRequest>()

                // Валидация
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

            // POST /auth/login - вход
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

        // ============ ЗАЩИЩЁННЫЕ ЭНДПОИНТЫ ============
        authenticate("auth-jwt") {

            // ============ ПРОФИЛЬ ПОЛЬЗОВАТЕЛЯ ============
            route("/users/me") {

                // GET /users/me - профиль
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

                // GET /users/me/prizes - избранные премии
                get("/prizes") {
                    val userId = call.getUserId() ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse("Unauthorized", "Invalid token")
                    )

                    val favorites = getUserFavoritesUseCase(userId)
                    val summaries = favorites.map { it.toSummaryResponse() }

                    call.respond(HttpStatusCode.OK, summaries)
                }

                // POST /users/me/prizes/{prizeId} - добавить в избранное
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

                    // Проверяем существование премии
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

                // DELETE /users/me/prizes/{prizeId} - удалить из избранного
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

            // ============ ПРЕМИИ ============
            route("/prizes") {

                // GET /prizes - список всех премий
                get {
                    val prizes = getPrizesUseCase()
                    val summaries = prizes.map { it.toSummaryResponse() }
                    call.respond(HttpStatusCode.OK, summaries)
                }

                // GET /prizes/{year}/{category} - детальная информация
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

                    val prize = getPrizeByYearAndCategoryUseCase(year, category)

                    if (prize != null) {
                        // Получаем лауреатов
                        val laureates = prizeRepository.getLaureates(prize.id!!)
                        val fullPrize = prize.copy(laureates = laureates)
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
                }

                // GET /prizes/{year}/{category}/laureates - список лауреатов
                get("/{year}/{category}/laureates") {
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

                    val prize = getPrizeByYearAndCategoryUseCase(year, category)

                    if (prize != null) {
                        val laureates = prizeRepository.getLaureates(prize.id!!)
                        call.respond(
                            HttpStatusCode.OK,
                            LaureatesResponse(
                                year = year,
                                category = category,
                                laureates = laureates
                            )
                        )
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

// ============ РАСШИРЕНИЯ ============

/**
 * Получить ID пользователя из JWT токена
 */
private fun ApplicationCall.getUserId(): Int? {
    val principal = principal<JWTPrincipal>() ?: return null
    val userId = principal.payload.getClaim("userId").asInt()
    return if (userId == 0) null else userId
}

/**
 * Преобразование NobelPrize в PrizeSummaryResponse
 */
private fun com.example.domain.model.NobelPrize.toSummaryResponse(): PrizeSummaryResponse {
    return PrizeSummaryResponse(
        awardYear = this.awardYear,
        category = this.category,
        prizeAmount = this.prizeAmount ?: 0L,
        laureatesCount = this.laureates.size
    )
}