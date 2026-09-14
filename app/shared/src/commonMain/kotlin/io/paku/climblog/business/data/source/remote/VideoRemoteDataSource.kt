package io.paku.climblog.business.data.source.remote

import io.paku.climblog.business.domain.model.comment.Comment
import io.paku.climblog.business.domain.model.comment.CommentFeed
import io.paku.climblog.business.domain.model.video.Video
import io.paku.climblog.business.domain.model.video.VideoFeed
import io.paku.climblog.business.remote.dto.response.video.PresignedPostResponse

interface VideoRemoteDataSource {
    suspend fun getPresignedPost(
        fileName: String,
        contentType: String
    ): PresignedPostResponse

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
    ): Video

    // Feed & Interactions
    suspend fun getVideos(
        type: String? = null,
        userId: Long? = null,
        sortBy: String = "CREATED_AT",
        orderBy: String = "DESC",
        cursor: Long? = null,
        limit: Int = 10
    ): VideoFeed

    suspend fun toggleLike(videoId: Long): Boolean
    suspend fun getComments(videoId: Long, cursor: Long?, limit: Int): CommentFeed
    suspend fun postComment(videoId: Long, content: String): Comment
}
