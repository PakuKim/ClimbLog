package io.paku.climblog.domain.interactor.auth

import io.ktor.http.HttpStatusCode
import io.paku.climblog.domain.RefreshTokenRepository
import io.paku.climblog.domain.model.AppException
import io.paku.climblog.domain.model.token.AuthToken
import io.paku.climblog.domain.provider.JwtTokenProvider

internal class RefreshTokenUseCase(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {
    suspend operator fun invoke(
        refreshToken: String
    ): AuthToken {
        val userId = jwtTokenProvider.verifyAndExtractUserId(refreshToken) ?:
            throw AppException(HttpStatusCode.Unauthorized, "Invalid or expired refresh token")

        refreshTokenRepository.delete(userId)

        val newTokens = jwtTokenProvider.generateToken(userId)
        refreshTokenRepository.update(
            userId = userId,
            newRefreshToken = newTokens.refreshToken
        )

        return newTokens
    }
}