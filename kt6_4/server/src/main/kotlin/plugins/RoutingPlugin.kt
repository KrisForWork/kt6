package com.example.plugins

import com.example.data.repository.InMemoryPrizeRepository
import com.example.domain.usecase.*
import com.example.models.dto.*
import com.example.security.JwtTokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    // Инициализация зависимостей
    val prizeRepository = InMemoryPrizeRepository()
    val jwtTokenService = JwtTokenService()

    // Use Cases
    val getPrizesUseCase = GetPrizesUseCase(prizeRepository)
    val getPrizeByYearAndCategoryUseCase = GetPrizeByYearAndCategoryUseCase(prizeRepository)
    val loginUseCase = LoginUseCase(jwtTokenService)

    routing {
        // Корневой эндпоинт (публичный)
        get("/") {
            call.respondText(
                text = "Nobel Prize API Server",
                contentType = ContentType.Text.Plain
            )
        }

        // Health check (публичный)
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
            post("/login") {
                val request = call.receive<LoginRequest>()

                when (val result = loginUseCase(request.username, request.password)) {
                    is LoginUseCase.LoginResult.Success -> {
                        call.respond(
                            HttpStatusCode.OK,
                            LoginResponse(
                                token = result.token,
                                username = request.username
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
                    is LoginUseCase.LoginResult.UserNotFound -> {
                        call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse(
                                error = "User not found",
                                message = "User with username '${request.username}' does not exist"
                            )
                        )
                    }
                }
            }
        }

        // ============ ЗАЩИЩЁННЫЕ ЭНДПОИНТЫ ============
        authenticate("auth-jwt") {
            route("/prizes") {

                // GET /prizes - список всех премий
                get {
                    val prizes = getPrizesUseCase()
                    // Преобразуем в краткий формат для лучшей читаемости
                    val summaries = prizes.map { it.toSummaryResponse() }
                    call.respond(HttpStatusCode.OK, summaries)
                }

                // GET /prizes/{year}/{category} - детальная информация о премии
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
                        call.respond(HttpStatusCode.OK, prize)
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

                    val laureates = getPrizeByYearAndCategoryUseCase.getLaureates(year, category)
                    val prize = getPrizeByYearAndCategoryUseCase(year, category)

                    if (prize != null) {
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

private fun com.example.domain.model.NobelPrize.toSummaryResponse(): PrizeSummaryResponse {
    return PrizeSummaryResponse(
        awardYear = this.awardYear,
        category = this.category.name,
        prizeAmount = this.prizeAmount,
        laureatesCount = this.laureates.size
    )
}