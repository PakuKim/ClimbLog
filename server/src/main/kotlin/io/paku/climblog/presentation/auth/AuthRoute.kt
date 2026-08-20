package io.paku.climblog.presentation.auth

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.paku.climblog.domain.interactor.auth.LogoutUseCase
import io.paku.climblog.domain.interactor.auth.RefreshTokenUseCase
import io.paku.climblog.domain.interactor.auth.SocialLoginUseCase
import io.paku.climblog.domain.interactor.auth.SocialRegisterUseCase
import org.koin.ktor.ext.inject

internal fun Route.authRoutes() {
    val refreshTokenUseCase: RefreshTokenUseCase by inject()
    val socialLoginUseCase: SocialLoginUseCase by inject()
    val socialRegisterUseCase: SocialRegisterUseCase by inject()
    val logoutUseCase: LogoutUseCase by inject()

    route("/api/v1/auth") {
        authenticate("auth-jwt") {
            post("/logout") {
                val userId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)
                logoutUseCase(userId)
                call.respond(HttpStatusCode.OK)
            }
        }

        post("/social/login") {
            val request = call.receive<SocialLoginRequest>()
            val result = socialLoginUseCase(
                provider = request.provider,
                socialToken = request.socialToken
            )

            call.respond(
                HttpStatusCode.OK,
                AuthResponse(
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken,
                )
            )
        }

        post("/social/register") {
            val request = call.receive<SocialRegisterRequest>()
            socialRegisterUseCase(
                provider = request.provider,
                socialToken = request.socialToken,
                handle = request.handle,
                name = request.name,
                age = request.age,
                height = request.height,
                armReach = request.armReach,
                gender = request.gender,
                profilePhotoUrl = request.profilePhotoUrl
            )

            call.respond(HttpStatusCode.Created, "User registered successfully")
        }

        post("/refresh") {
            val request = call.receive<RefreshTokenRequest>()
            val rawRefreshToken = request.refreshToken.removePrefix("Bearer ").trim()

            val tokens = refreshTokenUseCase(rawRefreshToken)
            call.respond(
                HttpStatusCode.OK,
                AuthResponse(
                    accessToken = tokens.accessToken,
                    refreshToken = tokens.refreshToken
                )
            )
        }
    }
}
