package io.paku.climblog.business.domain.interactors.auth

import io.paku.climblog.business.domain.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}
