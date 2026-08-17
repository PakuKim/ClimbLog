package io.paku.climblog.domain.interactor.user

import io.paku.climblog.domain.RefreshTokenRepository
import io.paku.climblog.domain.UserRepository

class DeleteUserUseCase(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    suspend operator fun invoke(userId: Long): Result<Unit> = runCatching {
        userRepository.delete(userId)
        refreshTokenRepository.delete(userId)
    }
}
