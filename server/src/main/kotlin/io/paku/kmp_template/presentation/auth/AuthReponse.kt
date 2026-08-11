package io.paku.kmp_template.presentation.auth

import kotlinx.serialization.Serializable

@Serializable
data class CheckEmailResponse(
    val isAvailable: Boolean
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)

@Serializable
data class UserResponse(
    val id: Long,
    val email: String,
    val name: String
)