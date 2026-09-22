package io.paku.climblog.domain.interactors.user

class FetchUserUseCase(
    private val repository: io.paku.climblog.domain.UserRepository
) {
    operator fun invoke() = repository.fetchUserData()
}