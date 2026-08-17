package io.paku.climblog.domain.interactor.video

import io.paku.climblog.domain.CommentRepository
import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.interactor.notification.SendNotificationUseCase
import io.paku.climblog.domain.model.Comment

class PostCommentUseCase(
    private val commentRepository: CommentRepository,
    private val videoRepository: VideoRepository,
    private val sendNotificationUseCase: SendNotificationUseCase
) {
    suspend operator fun invoke(
        userId: Long,
        videoId: Long,
        content: String
    ): Result<Comment> = runCatching {
        val comment = Comment(
            videoId = videoId,
            userId = userId,
            userName = "", // Filled by repo
            userProfilePhotoUrl = null,
            content = content,
            createdAt = System.currentTimeMillis()
        )
        
        val savedComment = commentRepository.save(comment)
        
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
