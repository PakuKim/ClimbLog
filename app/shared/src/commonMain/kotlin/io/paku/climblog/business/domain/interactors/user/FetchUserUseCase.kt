package io.paku.climblog.business.domain.interactors.user

import io.paku.climblog.business.domain.UserRepository

class FetchUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke() = repository.getUser()
}