package io.paku.climblog.data

import io.paku.climblog.data.source.remote.AuthRemoteDataSource
import io.paku.climblog.domain.AuthRepository
import io.paku.climblog.domain.SessionRepository
import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.social.SocialLoginType

internal class AuthRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository
): AuthRepository {
    override suspend fun socialLogin(
        socialLoginType: SocialLoginType,
        socialToken: String
    ) {
        val authData = authRemoteDataSource.socialLogin(
            provider = socialLoginType.name,
            socialToken = socialToken
        )

        sessionRepository.saveSession(authData.accessToken, authData.refreshToken)

        userRepository.getUser()
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
//        authRemoteDataSource.logout()
        sessionRepository.clearAll()
    }
}
