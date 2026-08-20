package io.paku.climblog.domain.model

import io.ktor.http.HttpStatusCode

open class AppException(
    val statusCode: HttpStatusCode,
    override val message: String
) : RuntimeException(message)