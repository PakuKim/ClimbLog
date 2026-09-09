package io.paku.climblog.domain.ext

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.routing.RoutingCall
import io.paku.climblog.domain.model.AppException

internal fun RoutingCall.getUserId(): Long {
    return this.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull()
        ?: throw AppException(HttpStatusCode.Unauthorized, "Invalid token")
}