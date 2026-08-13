package io.paku.kmp_template.presentation.auth

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.paku.kmp_template.domain.interactor.auth.CheckEmailUseCase
import io.paku.kmp_template.domain.interactor.auth.LoginUseCase
import io.paku.kmp_template.domain.interactor.auth.RefreshTokenUseCase
import io.paku.kmp_template.domain.interactor.auth.RegisterUseCase
import org.koin.ktor.ext.inject

internal fun Route.authRoutes() {
    val refreshTokenUseCase: RefreshTokenUseCase by inject()
    val checkEmailUseCase: CheckEmailUseCase by inject()
    val registerUseCase: RegisterUseCase by inject()
    val loginUseCase: LoginUseCase by inject()

    route("/auth") {
        post("/refresh") {
            val request = call.receive<RefreshTokenRequest>()
            val rawRefreshToken = request.refreshToken.removePrefix("Bearer ").trim()

            refreshTokenUseCase(rawRefreshToken)
                .onSuccess { tokens ->
                    call.respond(
                        HttpStatusCode.OK,
                        AuthResponse(accessToken = tokens.accessToken, refreshToken = tokens.refreshToken)
                    )
                }
                .onFailure {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid refresh token"))
                }
        }

        post("/check-email") {
            val request = call.receive<CheckEmailRequest>()
            val isAvailable = checkEmailUseCase(request.email)

            call.respond(HttpStatusCode.OK, CheckEmailResponse(isAvailable = isAvailable))
        }

        post("/register") {
            val request = call.receive<RegisterRequest>()
            registerUseCase(request.email, request.password, request.name)
                .onSuccess { user ->
                    call.respond(
                        HttpStatusCode.Created,
                        UserResponse(id = user.id, email = user.email, name = user.name)
                    )
                }
                .onFailure { error ->
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to (error.message ?: "Registration failed")))
                }
        }

        post("/login") {
            val request = call.receive<LoginRequest>()

            loginUseCase(request.email, request.password)
                .onSuccess { tokens ->
                    call.respond(
                        HttpStatusCode.OK,
                        AuthResponse(accessToken = tokens.accessToken, refreshToken = tokens.refreshToken)
                    )
                }
                .onFailure {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
                }
        }
    }
}
