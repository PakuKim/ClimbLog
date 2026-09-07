package io.paku.climblog.business.domain

import io.paku.climblog.business.domain.model.Comment
import io.paku.climblog.business.domain.model.Video
import io.paku.climblog.business.domain.model.video.PresignedPostResponse

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
    suspend fun getFeed(cursor: Long?, limit: Int): Result<List<Video>>
    suspend fun getRandomVideos(limit: Int): Result<List<Video>>
    suspend fun getUserVideos(userId: Long): Result<List<Video>>
    suspend fun toggleLike(videoId: Long): Result<Boolean>
    suspend fun getComments(videoId: Long): Result<List<Comment>>
    suspend fun postComment(videoId: Long, content: String): Result<Comment>

    // Legacy Single Upload
    suspend fun getPresignedUrl(fileName: String, contentType: String): Result<Pair<String, String>>
    suspend fun uploadToS3(url: String, bytes: ByteArray, onProgress: (Float) -> Unit): Result<Unit>
}
