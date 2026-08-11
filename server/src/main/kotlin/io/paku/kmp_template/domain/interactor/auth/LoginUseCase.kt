package io.paku.kmp_template.domain.interactor.auth

import io.paku.kmp_template.domain.UserRepository
import io.paku.kmp_template.domain.model.AuthToken
import io.paku.kmp_template.domain.provider.BCryptEncodeProvider
import io.paku.kmp_template.domain.provider.JwtTokenProvider

class LoginUseCase(
    private val userRepository: UserRepository,
    private val bCryptEncodeProvider: BCryptEncodeProvider,
    private val jwtTokenProvider: JwtTokenProvider
) {
    suspend operator fun invoke(email: String, rawPassword: String): Result<AuthToken> {
        val user = userRepository.findByEmail(email)
            ?: return Result.failure(IllegalArgumentException("Invalid email or password"))

        if (!bCryptEncodeProvider.verify(rawPassword, user.passwordHash)) {
            return Result.failure(IllegalArgumentException("Invalid email or password"))
        }

        val tokens = jwtTokenProvider.generateToken(user.id, user.email)
        return Result.success(tokens)
    }
}