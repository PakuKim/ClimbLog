package io.paku.climblog.domain.interactors.user

import io.paku.climblog.domain.UserRepository

class FetchUserUseCase(
    private val repository: UserRepository
) {
    operator fun invoke() = repository.fetchUserData()
}