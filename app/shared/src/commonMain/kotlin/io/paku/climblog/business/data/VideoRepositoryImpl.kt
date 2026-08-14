package io.paku.climblog.business.data

import io.paku.climblog.business.data.source.remote.VideoRemoteDataSource
import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.Comment
import io.paku.climblog.business.domain.model.Video

internal class VideoRepositoryImpl(
    private val videoRemoteDataSource: VideoRemoteDataSource
) : VideoRepository {

    override suspend fun getPresignedUrl(fileName: String, contentType: String): Result<Pair<String, String>> = runCatching {
        videoRemoteDataSource.getPresignedUrl(fileName, contentType)
    }

    override suspend fun uploadToS3(url: String, bytes: ByteArray, onProgress: (Float) -> Unit): Result<Unit> = runCatching {
        videoRemoteDataSource.uploadToS3(url, bytes, onProgress)
    }

    override suspend fun registerVideo(
        title: String,
        description: String?,
        s3Key: String,
        cruxStartTime: Double?,
        cruxEndTime: Double?
    ): Result<Video> = runCatching {
        videoRemoteDataSource.registerVideo(
            title = title,
            description = description,
            s3Key = s3Key,
            cruxStartTime = cruxStartTime,
            cruxEndTime = cruxEndTime
        )
    }

    override suspend fun getFeed(cursor: Long?, limit: Int): Result<List<Video>> = runCatching {
        videoRemoteDataSource.getFeed(cursor, limit)
    }

    override suspend fun getRandomVideos(limit: Int): Result<List<Video>> = runCatching {
        videoRemoteDataSource.getRandomVideos(limit)
    }

    override suspend fun getUserVideos(userId: Long): Result<List<Video>> = runCatching {
        videoRemoteDataSource.getUserVideos(userId)
    }

    override suspend fun toggleLike(videoId: Long): Result<Boolean> = runCatching {
        videoRemoteDataSource.toggleLike(videoId)
    }

    override suspend fun getComments(videoId: Long): Result<List<Comment>> = runCatching {
        videoRemoteDataSource.getComments(videoId)
    }

    override suspend fun postComment(videoId: Long, content: String): Result<Comment> = runCatching {
        videoRemoteDataSource.postComment(videoId, content)
    }
}
