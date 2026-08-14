package io.paku.climblog.domain.model

data class AuthToken(
    val accessToken: String,
    val refreshToken: String
)