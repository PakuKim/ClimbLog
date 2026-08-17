package io.paku.climblog.business.domain.model

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
