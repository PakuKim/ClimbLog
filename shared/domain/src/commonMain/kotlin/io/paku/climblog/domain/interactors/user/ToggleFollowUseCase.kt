package io.paku.climblog.domain.interactors.user

class ToggleFollowUseCase(
    private val userRepository: io.paku.climblog.domain.UserRepository
) {
    suspend operator fun invoke(userId: Long, isFollowing: Boolean) {
        userRepository.toggleFollow(userId, isFollowing)
    }
}
