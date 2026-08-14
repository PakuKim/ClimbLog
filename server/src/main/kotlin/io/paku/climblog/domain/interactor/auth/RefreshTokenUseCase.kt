package io.paku.climblog.domain.interactor.auth

import io.paku.climblog.domain.RefreshTokenRepository
import io.paku.climblog.domain.model.AuthToken
import io.paku.climblog.domain.provider.JwtTokenProvider

internal class RefreshTokenUseCase(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {
    suspend operator fun invoke(
        refreshToken: String
    ): Result<AuthToken> {
        val userId = jwtTokenProvider.verifyAndExtractUserId(refreshToken) ?:
        return Result.failure(
            IllegalArgumentException("Invalid or expired refresh token")
        )

        refreshTokenRepository.delete(userId)

        val newTokens = jwtTokenProvider.generateToken(userId)
        refreshTokenRepository.update(
            userId = userId,
            newRefreshToken = newTokens.refreshToken
        )

        return Result.success(newTokens)
    }
}