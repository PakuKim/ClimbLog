package io.paku.climblog.domain.interactor.auth

import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.User
import io.paku.climblog.domain.provider.BCryptEncodeProvider

class RegisterUseCase(
    private val userRepository: UserRepository,
    private val bCryptEncodeProvider: BCryptEncodeProvider
) {
    suspend operator fun invoke(email: String, rawPassword: String, name: String): Result<User> {
        if (userRepository.findByEmail(email) != null) {
            return Result.failure(IllegalArgumentException("Email already exists"))
        }

        val hashedPassword = bCryptEncodeProvider.hash(rawPassword)
        val newUser = User(
            email = email,
            passwordHash = hashedPassword,
            name = name
        )

        val savedUser = userRepository.save(newUser)
        return Result.success(savedUser)
    }
}