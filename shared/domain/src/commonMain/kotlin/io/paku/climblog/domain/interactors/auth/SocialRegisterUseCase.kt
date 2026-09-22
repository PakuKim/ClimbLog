package io.paku.climblog.domain.interactors.auth

class SocialRegisterUseCase(
    private val authRepository: io.paku.climblog.domain.AuthRepository,
    private val socialLoginProvider: io.paku.climblog.domain.provider.social.SocialLoginProvider
) {
    suspend operator fun invoke(
        type: io.paku.climblog.domain.model.social.SocialLoginType,
        handle: String,
        name: String,
        age: Int,
        height: Int,
        armReach: Int,
        gender: String,
        profilePhotoUrl: String?
    ) {
        val socialLoginResult = socialLoginProvider.latestLoginResult(type)

        authRepository.socialRegister(
            socialToken = socialLoginResult.token,
            socialLoginType = socialLoginResult.type,
            handle = handle,
            name = name,
            age = age,
            height = height,
            armReach = armReach,
            gender = gender,
            profilePhotoUrl = profilePhotoUrl
        )

        authRepository.socialLogin(
            socialToken = socialLoginResult.token,
            socialLoginType = socialLoginResult.type
        )
    }
}
