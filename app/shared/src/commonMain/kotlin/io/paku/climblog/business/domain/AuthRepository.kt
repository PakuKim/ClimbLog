package io.paku.climblog.business.domain

import io.paku.climblog.business.domain.model.SocialLoginType

interface AuthRepository {
    suspend fun socialLogin(
        socialLoginType: SocialLoginType,
        socialToken: String,
    )

    suspend fun socialRegister(
        socialToken: String,
        socialLoginType: SocialLoginType,
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
