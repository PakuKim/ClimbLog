package io.paku.climblog.business.domain

import androidx.paging.PagingData
import io.paku.climblog.business.domain.model.Comment
import io.paku.climblog.business.domain.model.video.PresignedPostResponse
import io.paku.climblog.business.domain.model.video.Video
import io.paku.climblog.business.domain.model.video.VideoFeed
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    // S3 POST Upload (Direction A)
    suspend fun getPresignedPost(
        fileName: String,
        contentType: String
    ): Result<PresignedPostResponse>

    suspend fun uploadVideoToS3Post(
        url: String,
        fields: Map<String, String>,
        videoBytes: ByteArray,
        onProgress: (Float) -> Unit
    ): Result<Unit>

    suspend fun registerVideo(
        title: String,
        description: String?,
        s3Key: String,
        cruxStartTime: Double?,
        cruxEndTime: Double?
    ): Result<Video>

    // Feed & Interactions
    suspend fun getVideos(
        type: String? = null,
        userId: Long? = null,
        sortBy: String = "CREATED_AT",
        orderBy: String = "DESC",
        cursor: Long? = null,
        limit: Int = 10
    ): Result<VideoFeed>

    fun getVideosPaging(
        type: String? = null,
        userId: Long? = null,
        sortBy: String = "CREATED_AT",
        orderBy: String = "DESC",
        pageSize: Int = 10
    ): Flow<PagingData<Video>>

    suspend fun toggleLike(videoId: Long): Result<Boolean>
    suspend fun getComments(videoId: Long): Result<List<Comment>>
    suspend fun postComment(videoId: Long, content: String): Result<Comment>
}
