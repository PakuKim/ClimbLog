package io.paku.climblog.domain.interactor.user

import io.paku.climblog.domain.UserFollowRepository

class GetFollowStatusUseCase(
    private val userFollowRepository: UserFollowRepository
) {
    suspend operator fun invoke(currentUserId: Long, targetUserId: Long): Result<Boolean> = runCatching {
        userFollowRepository.isFollowing(currentUserId, targetUserId)
    }
}