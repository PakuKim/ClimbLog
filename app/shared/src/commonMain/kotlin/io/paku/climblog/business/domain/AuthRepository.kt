package io.paku.climblog.business.domain

import io.paku.climblog.business.domain.model.SocialAuthType

interface AuthRepository {
    suspend fun socialLogin(
        socialAuthType: SocialAuthType,
        socialToken: String,
    )

    suspend fun socialRegister(
        socialToken: String,
        socialAuthType: SocialAuthType,
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
