package io.paku.climblog.domain.interactor.auth

import io.paku.climblog.domain.UserRepository

class CheckEmailUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String): Boolean {
        return userRepository.findByEmail(email) == null
    }
}