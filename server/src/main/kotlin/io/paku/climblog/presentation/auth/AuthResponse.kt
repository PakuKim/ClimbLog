package io.paku.climblog.presentation.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val isRegistered: Boolean = true
)

@Serializable
data class CheckEmailResponse(
    val isAvailable: Boolean
)

@Serializable
data class UserResponse(
    val id: Long,
    val email: String,
    val name: String
)
