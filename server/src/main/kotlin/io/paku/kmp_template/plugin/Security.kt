package io.paku.kmp_template.plugin

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import io.paku.kmp_template.domain.provider.JwtTokenProvider
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val tokenManager: JwtTokenProvider by inject()

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(tokenManager.generateVerifier())
            validate { credential ->
                if (!credential.payload.subject.isNullOrEmpty()) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}
