package io.paku.climblog.business.domain.model

import kotlinx.serialization.Serializable

enum class SocialProvider {
    GOOGLE, KAKAO, NAVER
}

data class SocialAuthResult(
    val provider: SocialProvider,
    val accessToken: String?,
    val idToken: String?,
    val email: String,
    val name: String
)

@Serializable
data class SocialLoginServerResult(
    val isNewUser: Boolean,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val registerToken: String? = null,
    val provider: String? = null,
    val providerId: String? = null,
    val email: String? = null,
    val name: String? = null
)
