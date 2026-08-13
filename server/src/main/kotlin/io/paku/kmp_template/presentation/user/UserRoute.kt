package io.paku.kmp_template.presentation.user

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.paku.kmp_template.domain.interactor.user.GetUserUseCase
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val getUserUseCase: GetUserUseCase by inject()

    authenticate("auth-jwt") {
        route("/user") {
            get("/me") {
                val authHeaders = call.request.headers.getAll("Authorization")
                println("Authorization Headers: $authHeaders")

                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.subject?.toLongOrNull()
                if (userId == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf("error" to "Invalid user ID format in token")
                    )
                    return@get
                }

                getUserUseCase(userId)
                    .onSuccess { user ->
                        call.respond(
                            HttpStatusCode.OK,
                            UserResponse(
                                id = user.id,
                                email = user.email,
                                name = user.name
                            )
                        )
                    }
                    .onFailure {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                    }
            }
        }
    }
}