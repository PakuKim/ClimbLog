package io.paku.climblog.domain.interactor.notification

import io.paku.climblog.domain.NotificationRepository
import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.Notification
import io.paku.climblog.domain.provider.PushProvider

class SendNotificationUseCase(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    private val pushProvider: PushProvider
) {
    suspend operator fun invoke(
        userId: Long,
        type: String,
        fromUserId: Long,
        videoId: Long? = null
    ) {
        if (userId == fromUserId) return

        val fromUser = userRepository.findById(fromUserId) ?: return
        
        val message = when (type) {
            "LIKE" -> "${fromUser.name}님이 회원님의 영상을 좋아합니다."
            "COMMENT" -> "${fromUser.name}님이 댓글을 남겼습니다."
            "FOLLOW" -> "${fromUser.name}님이 회원님을 팔로우하기 시작했습니다."
            else -> "${fromUser.name}님이 상호작용했습니다."
        }

        val notification = Notification(
            userId = userId,
            type = type,
            fromUserId = fromUserId,
            fromUserName = fromUser.name,
            fromUserProfilePhotoUrl = fromUser.profilePhotoUrl,
            videoId = videoId,
            isRead = false,
        )

        notificationRepository.save(notification)

        val deviceToken = notificationRepository.getDeviceToken(userId)
        if (deviceToken != null) {
            pushProvider.sendPush(
                token = deviceToken,
                title = "ClimbLog",
                body = message,
                data = mapOf(
                    "type" to type,
                    "videoId" to (videoId?.toString() ?: ""),
                    "fromUserId" to fromUserId.toString()
                )
            )
        }
    }
}
