package io.paku.kmp_template.feature.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String
)

@Serializable
data class RegisterRequest(
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
    @SerialName("userName")
    val userName: String
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)