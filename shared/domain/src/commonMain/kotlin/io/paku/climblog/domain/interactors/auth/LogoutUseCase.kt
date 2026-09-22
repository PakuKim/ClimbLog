package io.paku.climblog.domain.interactors.auth

class LogoutUseCase(
    private val authRepository: io.paku.climblog.domain.AuthRepository,
    private val socialLoginProvider: io.paku.climblog.domain.provider.social.SocialLoginProvider
) {
    suspend operator fun invoke() {
        authRepository.logout()
        io.paku.climblog.domain.model.social.SocialLoginType.entries.forEach {
            socialLoginProvider.logout(it)
        }
    }
}
