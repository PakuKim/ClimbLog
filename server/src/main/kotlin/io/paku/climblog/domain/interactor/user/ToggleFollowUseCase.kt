package io.paku.climblog.domain.interactor.user

import io.paku.climblog.domain.UserFollowRepository
import io.paku.climblog.domain.interactor.notification.SendNotificationUseCase

class ToggleFollowUseCase(
    private val userFollowRepository: UserFollowRepository,
    private val sendNotificationUseCase: SendNotificationUseCase
) {
    suspend operator fun invoke(followerId: Long, followingId: Long): Result<Boolean> = runCatching {
        if (followerId == followingId) throw Exception("Cannot follow yourself")
        
        val isFollowing = if (userFollowRepository.isFollowing(followerId, followingId)) {
            userFollowRepository.unfollow(followerId, followingId)
            false
        } else {
            userFollowRepository.follow(followerId, followingId)
            true
        }

        if (isFollowing) {
            sendNotificationUseCase(
                userId = followingId,
                type = "FOLLOW",
                fromUserId = followerId
            )
        }
        
        isFollowing
    }
}
