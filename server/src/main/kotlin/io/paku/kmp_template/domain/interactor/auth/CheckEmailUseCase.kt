package io.paku.kmp_template.domain.interactor.auth

import io.paku.kmp_template.domain.UserRepository

class CheckEmailUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String): Boolean {
        return userRepository.findByEmail(email) == null
    }
}