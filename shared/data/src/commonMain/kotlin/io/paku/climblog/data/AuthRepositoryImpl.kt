package io.paku.climblog.data

internal class AuthRepositoryImpl(
    private val authRemoteDataSource: io.paku.climblog.data.source.remote.AuthRemoteDataSource,
    private val sessionRepository: io.paku.climblog.domain.SessionRepository,
    private val userRepository: io.paku.climblog.domain.UserRepository
): io.paku.climblog.domain.AuthRepository {
    override suspend fun socialLogin(
        socialLoginType: io.paku.climblog.domain.model.social.SocialLoginType,
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
        socialLoginType: io.paku.climblog.domain.model.social.SocialLoginType,
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
