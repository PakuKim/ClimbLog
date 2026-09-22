package io.paku.climblog.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    // S3 POST Upload (Direction A)
    suspend fun getPresignedPost(
        fileName: String,
        contentType: String
    ): Result<io.paku.climblog.domain.model.video.PresignedPost>

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
    ): Result<io.paku.climblog.domain.model.video.Video>

    // Feed & Interactions
    suspend fun getVideos(
        type: String? = null,
        userId: Long? = null,
        sortBy: String = "CREATED_AT",
        orderBy: String = "DESC",
        cursor: Long? = null,
        limit: Int = 10
    ): Result<io.paku.climblog.domain.model.video.VideoFeed>

    fun getVideosPaging(
        type: String? = null,
        userId: Long? = null,
        sortBy: String = "CREATED_AT",
        orderBy: String = "DESC",
        pageSize: Int = 10
    ): Flow<PagingData<io.paku.climblog.domain.model.video.Video>>

    suspend fun toggleLike(videoId: Long): Result<Boolean>
    suspend fun getComments(videoId: Long, cursor: Long?, limit: Int): Result<io.paku.climblog.domain.model.comment.CommentFeed>
    fun getCommentsPaging(videoId: Long, pageSize: Int = 20): Flow<PagingData<io.paku.climblog.domain.model.comment.Comment>>
    suspend fun postComment(videoId: Long, content: String): Result<io.paku.climblog.domain.model.comment.Comment>
}
