package io.paku.climblog.contract.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("refreshToken")
    val refreshToken: String
)

@Serializable
data class SocialLoginRequest(
    @SerialName("provider")
    val provider: String,
    @SerialName("socialToken")
    val socialToken: String
)

@Serializable
data class SocialRegisterRequest(
    @SerialName("socialToken")
    val socialToken: String,
    @SerialName("provider")
    val provider: String,
    @SerialName("handle")
    val handle: String,
    @SerialName("name")
    val name: String,
    @SerialName("age")
    val age: Int,
    @SerialName("height")
    val height: Int,
    @SerialName("armReach")
    val armReach: Int,
    @SerialName("gender")
    val gender: String,
    @SerialName("profilePhotoUrl")
    val profilePhotoUrl: String? = null
)

@Serializable
data class RefreshTokenRequest(
    @SerialName("refreshToken")
    val refreshToken: String
)
