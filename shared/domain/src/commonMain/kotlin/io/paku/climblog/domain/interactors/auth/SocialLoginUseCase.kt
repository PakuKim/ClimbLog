package io.paku.climblog.domain.interactors.auth

class SocialLoginUseCase(
    private val repository: io.paku.climblog.domain.AuthRepository,
    private val socialLoginProvider: io.paku.climblog.domain.provider.social.SocialLoginProvider
) {
    suspend operator fun invoke(type: io.paku.climblog.domain.model.social.SocialLoginType) {
        val socialLoginResult = socialLoginProvider.login(type)
        repository.socialLogin(
            socialLoginType = socialLoginResult.type,
            socialToken = socialLoginResult.token
        )
    }
}