package io.paku.climblog.domain.interactor.video

import io.paku.climblog.domain.VideoLikeRepository
import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.interactor.notification.SendNotificationUseCase

class ToggleLikeUseCase(
    private val videoLikeRepository: VideoLikeRepository,
    private val videoRepository: VideoRepository,
    private val sendNotificationUseCase: SendNotificationUseCase
) {
    suspend operator fun invoke(userId: Long, videoId: Long): Result<Boolean> = runCatching {
        val isLiked = videoLikeRepository.toggleLike(userId, videoId)
        
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
