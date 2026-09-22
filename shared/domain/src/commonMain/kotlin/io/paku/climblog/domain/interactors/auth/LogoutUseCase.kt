package io.paku.climblog.domain.interactors.auth

import io.paku.climblog.domain.AuthRepository
import io.paku.climblog.domain.model.social.SocialLoginType
import io.paku.climblog.domain.provider.social.SocialLoginProvider

class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val socialLoginProvider: SocialLoginProvider
) {
    suspend operator fun invoke() {
        authRepository.logout()
        SocialLoginType.entries.forEach {
            socialLoginProvider.logout(it)
        }
    }
}
