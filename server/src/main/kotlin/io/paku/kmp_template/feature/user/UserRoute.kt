package io.paku.kmp_template.feature.user

import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.userRoutes() {
    authenticate("auth-jwt") {
        route("/user") {

        }
    }
}