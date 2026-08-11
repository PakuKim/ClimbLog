package io.paku.kmp_template.plugin

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import io.paku.kmp_template.presentation.auth.authRoutes
import io.paku.kmp_template.presentation.user.userRoutes

fun Application.configureRouting() {
    routing {
        authRoutes()
        userRoutes()
    }
}