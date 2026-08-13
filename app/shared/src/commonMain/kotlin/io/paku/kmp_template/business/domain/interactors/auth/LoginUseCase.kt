package io.paku.kmp_template.business.domain.interactors.auth

import io.paku.kmp_template.business.domain.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ) = repository.login(email, password)
}