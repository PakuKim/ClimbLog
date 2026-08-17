package io.paku.climblog.domain.interactor.user

import io.paku.climblog.domain.FollowRepository
import io.paku.climblog.domain.interactor.notification.SendNotificationUseCase

class FollowUserUseCase(
    private val followRepository: FollowRepository,
    private val sendNotificationUseCase: SendNotificationUseCase
) {
    suspend operator fun invoke(followerId: Long, followingId: Long): Result<Unit> = runCatching {
        if (followerId == followingId) throw Exception("Cannot follow yourself")
        
        val newlyFollowed = followRepository.follow(followerId, followingId)
        
        if (newlyFollowed) {
            sendNotificationUseCase(
                userId = followingId,
                type = "FOLLOW",
                fromUserId = followerId
            )
        }
    }
}
