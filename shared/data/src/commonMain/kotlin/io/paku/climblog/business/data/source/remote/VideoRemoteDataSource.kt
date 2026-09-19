package io.paku.climblog.business.data.source.remote

import io.paku.climblog.business.data.model.comment.CommentData
import io.paku.climblog.business.data.model.comment.CommentFeedData
import io.paku.climblog.business.data.model.video.PresignedPostData
import io.paku.climblog.business.data.model.video.VideoData
import io.paku.climblog.business.data.model.video.VideoFeedData

interface VideoRemoteDataSource {
    suspend fun getPresignedPost(
        fileName: String,
        contentType: String
    ): PresignedPostData

    suspend fun uploadVideoToS3Post(
        url: String,
        fields: Map<String, String>,
        videoBytes: ByteArray,
        onProgress: (Float) -> Unit
    )

    suspend fun registerVideo(
        title: String,
        description: String?,
        s3Key: String,
        cruxStartTime: Double?,
        cruxEndTime: Double?
    ): VideoData

    // Feed & Interactions
    suspend fun getVideos(
        type: String? = null,
        userId: Long? = null,
        sortBy: String = "CREATED_AT",
        orderBy: String = "DESC",
        cursor: Long? = null,
        limit: Int = 10
    ): VideoFeedData

    suspend fun toggleLike(videoId: Long): Boolean
    suspend fun getComments(videoId: Long, cursor: Long?, limit: Int): CommentFeedData
    suspend fun postComment(videoId: Long, content: String): CommentData
}
