package io.paku.climblog.business.domain.interactors.user

import io.paku.climblog.business.domain.UserRepository

internal class FetchUserUseCase(
    private val repository: UserRepository
) {
    operator fun invoke() = repository.fetchUserData()
}