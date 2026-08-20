package io.paku.climblog.presentation.auth

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class SocialLoginRequest(
    val provider: String,
    val socialToken: String
)

@Serializable
data class SocialRegisterRequest(
    val provider: String,
    val socialToken: String,
    val handle: String,
    val name: String,
    val age: Int,
    val height: Int,
    val armReach: Int,
    val gender: String,
    val profilePhotoUrl: String? = null
)
