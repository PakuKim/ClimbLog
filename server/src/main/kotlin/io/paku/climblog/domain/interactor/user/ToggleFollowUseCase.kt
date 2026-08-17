package io.paku.climblog.domain.interactor.user

import io.paku.climblog.domain.FollowRepository
import io.paku.climblog.domain.interactor.notification.SendNotificationUseCase

class ToggleFollowUseCase(
    private val followRepository: FollowRepository,
    private val sendNotificationUseCase: SendNotificationUseCase
) {
    suspend operator fun invoke(followerId: Long, followingId: Long): Result<Boolean> = runCatching {
        if (followerId == followingId) throw Exception("Cannot follow yourself")
        
        val isFollowing = if (followRepository.isFollowing(followerId, followingId)) {
            followRepository.unfollow(followerId, followingId)
            false
        } else {
            followRepository.follow(followerId, followingId)
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
