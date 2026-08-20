package io.paku.climblog.domain.interactor.video

import io.paku.climblog.domain.VideoCommentRepository
import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.interactor.notification.SendNotificationUseCase
import io.paku.climblog.domain.model.video.VideoComment

class PostCommentUseCase(
    private val videoCommentRepository: VideoCommentRepository,
    private val videoRepository: VideoRepository,
    private val sendNotificationUseCase: SendNotificationUseCase
) {
    suspend operator fun invoke(
        userId: Long,
        videoId: Long,
        content: String
    ): Result<VideoComment> = runCatching {
        val videoComment = VideoComment(
            videoId = videoId,
            userId = userId,
            userName = "",
            userProfilePhotoUrl = null,
            content = content,
        )
        
        val savedComment = videoCommentRepository.save(videoComment)
        
        val video = videoRepository.findById(videoId)
        if (video != null) {
            sendNotificationUseCase(
                userId = video.userId,
                type = "COMMENT",
                fromUserId = userId,
                videoId = videoId
            )
        }
        
        savedComment
    }
}
