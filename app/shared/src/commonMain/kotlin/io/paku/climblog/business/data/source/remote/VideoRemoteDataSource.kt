package io.paku.climblog.business.data.source.remote

import io.paku.climblog.business.domain.model.Comment
import io.paku.climblog.business.domain.model.video.PresignedPostResponse
import io.paku.climblog.business.domain.model.video.Video
import io.paku.climblog.business.domain.model.video.VideoFeed

interface VideoRemoteDataSource {
    // S3 POST Upload (Direction A)
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
    suspend fun getComments(videoId: Long): List<Comment>
    suspend fun postComment(videoId: Long, content: String): Comment
}
