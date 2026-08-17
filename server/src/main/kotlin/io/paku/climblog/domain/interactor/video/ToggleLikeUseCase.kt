package io.paku.climblog.domain.interactor.video

import io.paku.climblog.domain.LikeRepository
import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.interactor.notification.SendNotificationUseCase

class ToggleLikeUseCase(
    private val likeRepository: LikeRepository,
    private val videoRepository: VideoRepository,
    private val sendNotificationUseCase: SendNotificationUseCase
) {
    suspend operator fun invoke(userId: Long, videoId: Long): Result<Boolean> = runCatching {
        val isLiked = likeRepository.toggleLike(userId, videoId)
        
        if (isLiked) {
            val video = videoRepository.findById(videoId)
            if (video != null) {
                sendNotificationUseCase(
                    userId = video.userId,
                    type = "LIKE",
                    fromUserId = userId,
                    videoId = videoId
                )
            }
        }
        
        isLiked
    }
}
