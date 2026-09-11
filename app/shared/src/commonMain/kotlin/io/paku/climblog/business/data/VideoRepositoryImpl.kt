package io.paku.climblog.business.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.paku.climblog.business.data.source.paging.VideoPagingSource
import io.paku.climblog.business.data.source.remote.VideoRemoteDataSource
import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.Comment
import io.paku.climblog.business.domain.model.video.PresignedPostResponse
import io.paku.climblog.business.domain.model.video.Video
import io.paku.climblog.business.domain.model.video.VideoFeed
import kotlinx.coroutines.flow.Flow

internal class VideoRepositoryImpl(
    private val videoRemoteDataSource: VideoRemoteDataSource
) : VideoRepository {

    override suspend fun getPresignedPost(fileName: String, contentType: String): Result<PresignedPostResponse> = runCatching {
        videoRemoteDataSource.getPresignedPost(fileName, contentType)
    }

    override suspend fun uploadVideoToS3Post(
        url: String,
        fields: Map<String, String>,
        videoBytes: ByteArray,
        onProgress: (Float) -> Unit
    ): Result<Unit> = runCatching {
        videoRemoteDataSource.uploadVideoToS3Post(url, fields, videoBytes, onProgress)
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

    override suspend fun getVideos(
        type: String?,
        userId: Long?,
        sortBy: String,
        orderBy: String,
        cursor: Long?,
        limit: Int
    ): Result<VideoFeed> = runCatching {
        videoRemoteDataSource.getVideos(
            type = type,
            userId = userId,
            sortBy = sortBy,
            orderBy = orderBy,
            cursor = cursor,
            limit = limit
        )
    }

    override fun getVideosPaging(
        type: String?,
        userId: Long?,
        sortBy: String,
        orderBy: String,
        pageSize: Int
    ): Flow<PagingData<Video>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                VideoPagingSource { cursor, limit ->
                    getVideos(
                        type = type,
                        userId = userId,
                        sortBy = sortBy,
                        orderBy = orderBy,
                        cursor = cursor,
                        limit = limit
                    )
                }
            }
        ).flow
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
