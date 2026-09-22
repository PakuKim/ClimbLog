package io.paku.climblog.domain.interactors.user

import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.user.User

class SearchUsersUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(query: String): List<User> {
        return userRepository.searchUsers(query)
    }
}
