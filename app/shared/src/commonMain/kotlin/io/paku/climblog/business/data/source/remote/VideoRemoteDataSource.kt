package io.paku.climblog.business.data.source.remote

import io.paku.climblog.business.domain.model.Comment
import io.paku.climblog.business.domain.model.video.PresignedPostResponse
import io.paku.climblog.business.domain.model.video.Video

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
    suspend fun getFeed(cursor: Long?, limit: Int): List<Video>
    suspend fun getRandomVideos(limit: Int): List<Video>
    suspend fun getUserVideos(userId: Long): List<Video>
    suspend fun toggleLike(videoId: Long): Boolean
    suspend fun getComments(videoId: Long): List<Comment>
    suspend fun postComment(videoId: Long, content: String): Comment

    // Legacy Single Upload (Optional Cleanup)
    suspend fun getPresignedUrl(fileName: String, contentType: String): Pair<String, String>
    suspend fun uploadToS3(url: String, bytes: ByteArray, onProgress: (Float) -> Unit)
}
