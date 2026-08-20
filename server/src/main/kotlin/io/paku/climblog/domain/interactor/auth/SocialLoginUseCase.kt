package io.paku.climblog.domain.interactor.auth

import io.ktor.http.HttpStatusCode
import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.AppException
import io.paku.climblog.domain.model.token.AuthToken
import io.paku.climblog.domain.provider.JwtTokenProvider

internal class SocialLoginUseCase(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val verifySocialTokenUseCase: VerifySocialTokenUseCase
) {
    suspend operator fun invoke(
        provider: String,
        socialToken: String
    ): AuthToken {
        val platform = provider.uppercase()
        val providerId = verifySocialTokenUseCase(platform, socialToken)

        val user = userRepository.findBySocialId(platform, providerId)
            ?: throw AppException(HttpStatusCode.NotFound, "User not found")

        return jwtTokenProvider.generateToken(user.id)
    }
}
