package io.paku.climblog.domain.interactors.user

class SearchUsersUseCase(
    private val userRepository: io.paku.climblog.domain.UserRepository
) {
    suspend operator fun invoke(query: String): List<io.paku.climblog.domain.model.user.User> {
        return userRepository.searchUsers(query)
    }
}
