package io.paku.climblog.business.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import io.paku.climblog.business.data.mapper.comment.CommentDataMapper
import io.paku.climblog.business.data.mapper.video.VideoDataMapper
import io.paku.climblog.business.data.source.paging.CommentPagingSource
import io.paku.climblog.business.data.source.paging.VideoPagingSource
import io.paku.climblog.business.data.source.remote.VideoRemoteDataSource
import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.comment.Comment
import io.paku.climblog.business.domain.model.comment.CommentFeed
import io.paku.climblog.business.domain.model.video.PresignedPost
import io.paku.climblog.business.domain.model.video.Video
import io.paku.climblog.business.domain.model.video.VideoFeed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class VideoRepositoryImpl(
    private val videoRemoteDataSource: VideoRemoteDataSource
) : VideoRepository {

    override suspend fun getPresignedPost(fileName: String, contentType: String): Result<PresignedPost> = runCatching {
        val data = videoRemoteDataSource.getPresignedPost(fileName, contentType)
        PresignedPost(
            url = data.url,
            fields = data.fields,
            objectKey = data.objectKey
        )
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
        val data = videoRemoteDataSource.registerVideo(
            title = title,
            description = description,
            s3Key = s3Key,
            cruxStartTime = cruxStartTime,
            cruxEndTime = cruxEndTime
        )
        VideoDataMapper.mapToRight(data)
    }

    override suspend fun getVideos(
        type: String?,
        userId: Long?,
        sortBy: String,
        orderBy: String,
        cursor: Long?,
        limit: Int
    ): Result<VideoFeed> = runCatching {
        val feedData = videoRemoteDataSource.getVideos(
            type = type,
            userId = userId,
            sortBy = sortBy,
            orderBy = orderBy,
            cursor = cursor,
            limit = limit
        )
        VideoFeed(
            items = feedData.items.map { VideoDataMapper.mapToRight(it) },
            nextCursor = feedData.nextCursor
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
                    runCatching {
                        videoRemoteDataSource.getVideos(
                            type = type,
                            userId = userId,
                            sortBy = sortBy,
                            orderBy = orderBy,
                            cursor = cursor,
                            limit = limit
                        )
                    }
                }
            }
        ).flow.map { pagingData ->
            pagingData.map { VideoDataMapper.mapToRight(it) }
        }
    }

    override suspend fun toggleLike(videoId: Long): Result<Boolean> = runCatching {
        videoRemoteDataSource.toggleLike(videoId)
    }

    override suspend fun getComments(videoId: Long, cursor: Long?, limit: Int): Result<CommentFeed> = runCatching {
        val feedData = videoRemoteDataSource.getComments(videoId, cursor, limit)
        CommentFeed(
            items = feedData.items.map { CommentDataMapper.mapToRight(it) },
            nextCursor = feedData.nextCursor
        )
    }

    override fun getCommentsPaging(videoId: Long, pageSize: Int): Flow<PagingData<Comment>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommentPagingSource { cursor, limit ->
                    runCatching {
                        videoRemoteDataSource.getComments(videoId, cursor, limit)
                    }
                }
            }
        ).flow.map { pagingData ->
            pagingData.map { CommentDataMapper.mapToRight(it) }
        }
    }

    override suspend fun postComment(videoId: Long, content: String): Result<Comment> = runCatching {
        val data = videoRemoteDataSource.postComment(videoId, content)
        CommentDataMapper.mapToRight(data)
    }
}
