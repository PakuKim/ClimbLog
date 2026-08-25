package io.paku.climblog.business.data

import io.paku.climblog.business.data.source.local.SessionLocalDataSource
import io.paku.climblog.business.data.source.remote.AuthRemoteDataSource
import io.paku.climblog.business.domain.AuthRepository
import io.paku.climblog.business.domain.model.SocialLoginType

internal class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val sessionLocal: SessionLocalDataSource
): AuthRepository {
    override suspend fun socialLogin(
        socialLoginType: SocialLoginType,
        socialToken: String
    ) {
        val (accessToken, refreshToken) = authRemoteDataSource.socialLogin(
            provider = socialLoginType.name,
            socialToken = socialToken
        )

        sessionLocal.saveAccessToken(accessToken)
        sessionLocal.saveRefreshToken(refreshToken)
    }

    override suspend fun socialRegister(
        socialToken: String,
        socialLoginType: SocialLoginType,
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
            provider = socialLoginType.name,
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
