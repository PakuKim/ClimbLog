package io.paku.kmp_template.plugin

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import io.paku.kmp_template.feature.auth.AuthService
import io.paku.kmp_template.feature.auth.authRoutes
import io.paku.kmp_template.feature.user.userRoutes

fun Application.configureRouting() {
    val authService = AuthService()

    routing {
        authRoutes(authService)
        userRoutes()
    }
}