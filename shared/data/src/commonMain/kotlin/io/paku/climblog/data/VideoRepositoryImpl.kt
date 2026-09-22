package io.paku.climblog.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class VideoRepositoryImpl(
    private val videoRemoteDataSource: io.paku.climblog.data.source.remote.VideoRemoteDataSource
) : io.paku.climblog.domain.VideoRepository {

    override suspend fun getPresignedPost(fileName: String, contentType: String): Result<io.paku.climblog.domain.model.video.PresignedPost> = runCatching {
        val data = videoRemoteDataSource.getPresignedPost(fileName, contentType)
        _root_ide_package_.io.paku.climblog.domain.model.video.PresignedPost(
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
    ): Result<io.paku.climblog.domain.model.video.Video> = runCatching {
        val data = videoRemoteDataSource.registerVideo(
            title = title,
            description = description,
            s3Key = s3Key,
            cruxStartTime = cruxStartTime,
            cruxEndTime = cruxEndTime
        )
        io.paku.climblog.data.mapper.video.VideoDataMapper.mapToRight(data)
    }

    override suspend fun getVideos(
        type: String?,
        userId: Long?,
        sortBy: String,
        orderBy: String,
        cursor: Long?,
        limit: Int
    ): Result<io.paku.climblog.domain.model.video.VideoFeed> = runCatching {
        val feedData = videoRemoteDataSource.getVideos(
            type = type,
            userId = userId,
            sortBy = sortBy,
            orderBy = orderBy,
            cursor = cursor,
            limit = limit
        )
        _root_ide_package_.io.paku.climblog.domain.model.video.VideoFeed(
            items = feedData.items.map {
                io.paku.climblog.data.mapper.video.VideoDataMapper.mapToRight(
                    it
                )
            },
            nextCursor = feedData.nextCursor
        )
    }

    override fun getVideosPaging(
        type: String?,
        userId: Long?,
        sortBy: String,
        orderBy: String,
        pageSize: Int
    ): Flow<PagingData<io.paku.climblog.domain.model.video.Video>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                _root_ide_package_.io.paku.climblog.data.source.paging.VideoPagingSource { cursor, limit ->
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
            pagingData.map { io.paku.climblog.data.mapper.video.VideoDataMapper.mapToRight(it) }
        }
    }

    override suspend fun toggleLike(videoId: Long): Result<Boolean> = runCatching {
        videoRemoteDataSource.toggleLike(videoId)
    }

    override suspend fun getComments(videoId: Long, cursor: Long?, limit: Int): Result<io.paku.climblog.domain.model.comment.CommentFeed> = runCatching {
        val feedData = videoRemoteDataSource.getComments(videoId, cursor, limit)
        _root_ide_package_.io.paku.climblog.domain.model.comment.CommentFeed(
            items = feedData.items.map {
                io.paku.climblog.data.mapper.comment.CommentDataMapper.mapToRight(
                    it
                )
            },
            nextCursor = feedData.nextCursor
        )
    }

    override fun getCommentsPaging(videoId: Long, pageSize: Int): Flow<PagingData<io.paku.climblog.domain.model.comment.Comment>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                _root_ide_package_.io.paku.climblog.data.source.paging.CommentPagingSource { cursor, limit ->
                    runCatching {
                        videoRemoteDataSource.getComments(videoId, cursor, limit)
                    }
                }
            }
        ).flow.map { pagingData ->
            pagingData.map { io.paku.climblog.data.mapper.comment.CommentDataMapper.mapToRight(it) }
        }
    }

    override suspend fun postComment(videoId: Long, content: String): Result<io.paku.climblog.domain.model.comment.Comment> = runCatching {
        val data = videoRemoteDataSource.postComment(videoId, content)
        io.paku.climblog.data.mapper.comment.CommentDataMapper.mapToRight(data)
    }
}
