package io.paku.climblog.plugin

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.paku.climblog.domain.model.AppException
import kotlinx.serialization.Serializable

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<AppException> { call, cause ->
            call.respond(
                status = cause.statusCode,
                message = ErrorResponse(
                    status = cause.statusCode.value,
                    error = cause.statusCode.description,
                    message = cause.message
                )
            )
        }

        exception<Throwable> { call, cause ->
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorResponse(
                    status = HttpStatusCode.InternalServerError.value,
                    error = "Internal Server Error",
                    message = cause.message ?: "An unexpected error occurred"
                )
            )
        }
    }
}

@Serializable
data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String
)