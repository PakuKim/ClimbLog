package io.paku.climblog.business.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.paku.climblog.business.data.source.paging.CommentPagingSource
import io.paku.climblog.business.data.source.paging.VideoPagingSource
import io.paku.climblog.business.data.source.remote.VideoRemoteDataSource
import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.comment.Comment
import io.paku.climblog.business.domain.model.comment.CommentFeed
import io.paku.climblog.business.domain.model.video.Video
import io.paku.climblog.business.domain.model.video.VideoFeed
import io.paku.climblog.business.remote.dto.response.video.PresignedPostResponse
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

    override suspend fun getComments(videoId: Long, cursor: Long?, limit: Int): Result<CommentFeed> = runCatching {
        videoRemoteDataSource.getComments(videoId, cursor, limit)
    }

    override fun getCommentsPaging(videoId: Long, pageSize: Int): Flow<PagingData<Comment>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommentPagingSource { cursor, limit ->
                    getComments(videoId, cursor, limit)
                }
            }
        ).flow
    }

    override suspend fun postComment(videoId: Long, content: String): Result<Comment> = runCatching {
        videoRemoteDataSource.postComment(videoId, content)
    }
}
