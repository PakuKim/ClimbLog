package io.paku.climblog.business.domain.interactors.auth

import io.paku.climblog.business.domain.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ) = repository.login(email, password)
}