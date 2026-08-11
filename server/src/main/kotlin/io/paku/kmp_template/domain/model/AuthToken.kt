package io.paku.kmp_template.domain.model

data class AuthToken(
    val accessToken: String,
    val refreshToken: String
)