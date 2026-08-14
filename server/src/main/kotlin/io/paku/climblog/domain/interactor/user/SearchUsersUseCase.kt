package io.paku.climblog.domain.interactor.user

import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.User

class SearchUsersUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(query: String): Result<List<User>> = runCatching {
        userRepository.search(query)
    }
}
