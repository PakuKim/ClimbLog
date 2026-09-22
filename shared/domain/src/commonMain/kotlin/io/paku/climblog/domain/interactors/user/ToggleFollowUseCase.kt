package io.paku.climblog.domain.interactors.user

import io.paku.climblog.domain.UserRepository

class ToggleFollowUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long, isFollowing: Boolean) {
        userRepository.toggleFollow(userId, isFollowing)
    }
}
