package io.paku.climblog.business.domain.interactors.auth

import io.paku.climblog.business.domain.AuthRepository
import io.paku.climblog.business.domain.model.SocialLoginType
import io.paku.climblog.core.SocialLoginProvider

internal class LogoutUseCase(
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
