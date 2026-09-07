package io.paku.climblog.business.domain.interactors.user

import io.paku.climblog.business.domain.UserRepository
import io.paku.climblog.business.domain.model.User

class SearchUsersUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(query: String): List<User> {
        return userRepository.searchUsers(query)
    }
}
