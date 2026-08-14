package io.paku.climblog.domain.interactor.auth

import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.AuthToken
import io.paku.climblog.domain.provider.BCryptEncodeProvider
import io.paku.climblog.domain.provider.JwtTokenProvider

class LoginUseCase(
    private val userRepository: UserRepository,
    private val bCryptEncodeProvider: BCryptEncodeProvider,
    private val jwtTokenProvider: JwtTokenProvider
) {
    suspend operator fun invoke(email: String, rawPassword: String): Result<AuthToken> {
        val user = userRepository.findByEmail(email)
            ?: return Result.failure(IllegalArgumentException("Invalid email or password"))

        val passwordHash = user.passwordHash ?: return Result.failure(IllegalArgumentException("User does not have a password"))

        if (!bCryptEncodeProvider.verify(rawPassword, passwordHash)) {
            return Result.failure(IllegalArgumentException("Invalid email or password"))
        }

        val tokens = jwtTokenProvider.generateToken(user.id)
        return Result.success(tokens)
    }
}
