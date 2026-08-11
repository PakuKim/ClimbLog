package io.paku.kmp_template.feature.auth

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            authService.register(request)
            call.respond(HttpStatusCode.OK, mapOf("message" to "User added successfully"))
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val response = authService.login(request)
            if (response != null) {
                call.respond(HttpStatusCode.OK, response)
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid credentials"))
            }
        }
    }
}
