package io.paku.climblog.business.domain.interactors.auth

import io.paku.climblog.business.domain.AuthRepository
import io.paku.climblog.business.domain.model.SocialLoginType
import io.paku.climblog.business.domain.provider.social.SocialLoginProvider

internal class SocialLoginUseCase(
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