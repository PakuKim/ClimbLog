package io.paku.climblog.data.source.remote

import io.paku.climblog.data.model.comment.CommentData
import io.paku.climblog.data.model.comment.CommentFeedData
import io.paku.climblog.data.model.video.PresignedPutData
import io.paku.climblog.data.model.video.VideoData
import io.paku.climblog.data.model.video.VideoFeedData

interface VideoRemoteDataSource {
    suspend fun getPresignedPut(
        fileName: String,
        contentType: String
    ): PresignedPutData

    suspend fun uploadVideoToR2Put(
        uploadUrl: String,
        contentType: String,
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
