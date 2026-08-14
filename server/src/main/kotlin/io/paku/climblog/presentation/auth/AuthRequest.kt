package io.paku.climblog.presentation.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class CheckEmailRequest(
    val email: String
)

@Serializable
data class SocialLoginRequest(
    val email: String,
    val name: String,
    val socialId: String,
    val provider: String
)
