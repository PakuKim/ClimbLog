package io.paku.climblog.domain

interface AuthRepository {
    suspend fun socialLogin(
        socialLoginType: io.paku.climblog.domain.model.social.SocialLoginType,
        socialToken: String,
    )

    suspend fun socialRegister(
        socialToken: String,
        socialLoginType: io.paku.climblog.domain.model.social.SocialLoginType,
        handle: String,
        name: String,
        age: Int,
        height: Int,
        armReach: Int,
        gender: String,
        profilePhotoUrl: String?
    )

    suspend fun logout()
}
