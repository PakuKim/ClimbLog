package io.paku.climblog.domain.interactor.user

import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.user.User

internal class SearchUsersUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(query: String): List<User> {
        return userRepository.search(query)
    }
}
