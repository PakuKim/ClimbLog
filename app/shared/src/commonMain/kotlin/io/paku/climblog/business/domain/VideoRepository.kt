package io.paku.climblog.business.domain

import io.paku.climblog.business.domain.model.Comment
import io.paku.climblog.business.domain.model.Video

interface VideoRepository {
    suspend fun getPresignedUrl(fileName: String, contentType: String): Result<Pair<String, String>> // url, s3Key
    suspend fun uploadToS3(url: String, bytes: ByteArray, onProgress: (Float) -> Unit): Result<Unit>
    suspend fun registerVideo(
        title: String,
        description: String?,
        s3Key: String,
        cruxStartTime: Double?,
        cruxEndTime: Double?
    ): Result<Video>

    suspend fun getFeed(cursor: Long?, limit: Int): Result<List<Video>>
    
    suspend fun getRandomVideos(limit: Int): Result<List<Video>>
    
    suspend fun getUserVideos(userId: Long): Result<List<Video>>
    
    suspend fun toggleLike(videoId: Long): Result<Boolean>
    
    suspend fun getComments(videoId: Long): Result<List<Comment>>
    
    suspend fun postComment(videoId: Long, content: String): Result<Comment>
}
