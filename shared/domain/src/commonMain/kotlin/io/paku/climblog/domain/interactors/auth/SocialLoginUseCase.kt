package io.paku.climblog.domain.interactors.auth

import io.paku.climblog.domain.AuthRepository
import io.paku.climblog.domain.model.social.SocialLoginType
import io.paku.climblog.domain.provider.social.SocialLoginProvider

class SocialLoginUseCase(
    private val repository: AuthRepository,
    private val socialLoginProvider: SocialLoginProvider
) {
    suspend operator fun invoke(type: SocialLoginType) {
        val socialLoginResult = socialLoginProvider.login(type)
        repository.socialLogin(
            socialLoginType = socialLoginResult.type,
            socialToken = socialLoginResult.token
        )
    }
}