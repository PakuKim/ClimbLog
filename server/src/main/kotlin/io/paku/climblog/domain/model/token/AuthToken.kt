package io.paku.climblog.domain.model.token

data class AuthToken(
    val accessToken: String,
    val refreshToken: String
)