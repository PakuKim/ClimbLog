package io.paku.climblog.business.data.source.remote

import io.paku.climblog.business.domain.model.Comment
import io.paku.climblog.business.domain.model.Video

interface VideoRemoteDataSource {
    suspend fun getPresignedUrl(fileName: String, contentType: String): Pair<String, String>
    suspend fun uploadToS3(url: String, bytes: ByteArray, onProgress: (Float) -> Unit)
    suspend fun registerVideo(
        title: String,
        description: String?,
        s3Key: String,
        cruxStartTime: Double?,
        cruxEndTime: Double?
    ): Video

    suspend fun getFeed(cursor: Long?, limit: Int): List<Video>
    
    suspend fun getRandomVideos(limit: Int): List<Video>
    
    suspend fun getUserVideos(userId: Long): List<Video>
    
    suspend fun toggleLike(videoId: Long): Boolean
    
    suspend fun getComments(videoId: Long): List<Comment>
    
    suspend fun postComment(videoId: Long, content: String): Comment
}
