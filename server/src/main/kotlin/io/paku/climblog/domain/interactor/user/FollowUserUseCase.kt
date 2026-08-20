package io.paku.climblog.domain.interactor.user

import io.ktor.http.HttpStatusCode
import io.paku.climblog.domain.UserFollowRepository
import io.paku.climblog.domain.interactor.notification.SendNotificationUseCase
import io.paku.climblog.domain.model.AppException

internal class FollowUserUseCase(
    private val userFollowRepository: UserFollowRepository,
    private val sendNotificationUseCase: SendNotificationUseCase
) {
    suspend operator fun invoke(followerId: Long, followingId: Long) {
        if (followerId == followingId) throw AppException(
            HttpStatusCode.BadRequest,
            "Cannot follow yourself"
        )
        
        val newlyFollowed = userFollowRepository.follow(followerId, followingId)
        
        if (newlyFollowed) {
            sendNotificationUseCase(
                userId = followingId,
                type = "FOLLOW",
                fromUserId = followerId
            )
        }
    }
}
