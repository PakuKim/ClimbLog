package io.paku.climblog.business.data

import io.paku.climblog.business.data.source.local.SessionLocalDataSource
import io.paku.climblog.business.data.source.remote.AuthRemoteDataSource
import io.paku.climblog.business.domain.AuthRepository
import io.paku.climblog.business.domain.model.SocialAuthType

internal class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val sessionLocal: SessionLocalDataSource
): AuthRepository {
    override suspend fun socialLogin(
        socialAuthType: SocialAuthType,
        socialToken: String
    ) {
        val (accessToken, refreshToken) = authRemoteDataSource.socialLogin(
            provider = socialAuthType.name,
            socialToken = socialToken
        )

        sessionLocal.saveAccessToken(accessToken)
        sessionLocal.saveRefreshToken(refreshToken)
    }

    override suspend fun socialRegister(
        socialToken: String,
        socialAuthType: SocialAuthType,
        handle: String,
        name: String,
        age: Int,
        height: Int,
        armReach: Int,
        gender: String,
        profilePhotoUrl: String?
    ) {
        authRemoteDataSource.socialRegister(
            socialToken = socialToken,
            provider = socialAuthType.name,
            handle = handle,
            name = name,
            age = age,
            height = height,
            armReach = armReach,
            gender = gender,
            profilePhotoUrl = profilePhotoUrl
        )
    }

    override suspend fun logout() {
        authRemoteDataSource.logout()
        sessionLocal.clear()
    }
}
