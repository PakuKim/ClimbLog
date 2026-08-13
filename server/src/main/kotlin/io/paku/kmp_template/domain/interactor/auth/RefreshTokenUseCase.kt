package io.paku.kmp_template.domain.interactor.auth

import io.paku.kmp_template.domain.RefreshTokenRepository
import io.paku.kmp_template.domain.model.AuthToken
import io.paku.kmp_template.domain.provider.JwtTokenProvider

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