package io.paku.climblog.business.domain.interactors.auth

import io.paku.climblog.business.domain.AuthRepository
import io.paku.climblog.business.domain.model.SocialAuthType
import io.paku.climblog.business.domain.provider.social.SocialLoginProvider

internal class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val socialLoginProvider: SocialLoginProvider
) {
    suspend operator fun invoke() {
        authRepository.logout()
        SocialAuthType.entries.forEach {
            socialLoginProvider.logout(it)
        }
    }
}
