package io.paku.climblog.domain.interactors.user

class GetFollowersUseCase(
    private val userRepository: io.paku.climblog.domain.UserRepository
) {
    suspend operator fun invoke(userId: Long): Result<List<io.paku.climblog.domain.model.user.User>> {
        return userRepository.getFollowers(userId)
    }
}
