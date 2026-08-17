package io.paku.climblog.domain.interactor.auth

import io.paku.climblog.domain.RefreshTokenRepository

class LogoutUseCase(
    private val refreshTokenRepository: RefreshTokenRepository
) {
    suspend operator fun invoke(userId: Long): Result<Unit> = runCatching {
        refreshTokenRepository.delete(userId)
    }
}
