package io.paku.climblog.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.paku.climblog.contract.comment.CommentFeedResponse
import io.paku.climblog.contract.comment.CommentResponse
import io.paku.climblog.contract.video.PresignedPutRequest
import io.paku.climblog.contract.video.PresignedPutResponse
import io.paku.climblog.contract.video.VideoFeedResponse
import io.paku.climblog.contract.video.VideoResponse
import io.paku.climblog.data.model.comment.CommentData
import io.paku.climblog.data.model.comment.CommentFeedData
import io.paku.climblog.data.model.video.PresignedPutData
import io.paku.climblog.data.model.video.VideoData
import io.paku.climblog.data.model.video.VideoFeedData
import io.paku.climblog.data.source.remote.VideoRemoteDataSource
import io.paku.climblog.remote.mapper.comment.CommentResponseMapper
import io.paku.climblog.remote.mapper.video.VideoResponseMapper
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

internal class VideoRemoteDataSourceImpl(
    private val client: HttpClient
) : VideoRemoteDataSource {
    private companion object {
        const val PRESIGNED_PUT = "videos/uploads/presigned-put"
        const val REGISTER = "videos"
    }

    override suspend fun getPresignedPut(fileName: String, contentType: String): PresignedPutData {
        val response = client.post(PRESIGNED_PUT) {
            setBody(PresignedPutRequest(fileName, contentType))
        }.body<PresignedPutResponse>()

        return PresignedPutData(
            uploadUrl = response.uploadUrl,
            objectKey = response.objectKey
        )
    }

    override suspend fun uploadVideoToR2Put(
        uploadUrl: String,
        contentType: String,
        videoBytes: ByteArray,
        onProgress: (Float) -> Unit
    ) {
        val uploadClient = HttpClient {
            install(HttpTimeout) {
                requestTimeoutMillis = 600_000 // 10 mins
            }
        }

        uploadClient.put(uploadUrl) {
            contentType(ContentType.parse(contentType))
            setBody(videoBytes)
            onUpload { bytesSentTotal, contentLength ->
                if (contentLength != null && contentLength > 0) {
                    onProgress(bytesSentTotal.toFloat() / contentLength.toFloat())
                }
            }
        }
    }

    override suspend fun registerVideo(
        title: String,
        description: String?,
        s3Key: String,
        cruxStartTime: Double?,
        cruxEndTime: Double?
    ): VideoData {
        return client.post(REGISTER) {
            setBody(
                buildJsonObject {
                    put("title", title)
                    put("description", description ?: "")
                    put("s3Key", s3Key)
                    putJsonArray("cruxes") {
                        if (cruxStartTime != null && cruxEndTime != null) {
                            addJsonObject {
                                put("startTime", cruxStartTime)
                                put("endTime", cruxEndTime)
                            }
                        }
                    }
                }
            )
        }.body<VideoResponse>().let { VideoResponseMapper.mapToRight(it) }
    }

    override suspend fun getVideos(
        type: String?,
        userId: Long?,
        sortBy: String,
        orderBy: String,
        cursor: Long?,
        limit: Int
    ): VideoFeedData {
        val response = client.get("videos") {
            parameter("type", type)
            parameter("userId", userId)
            parameter("sortBy", sortBy)
            parameter("orderBy", orderBy)
            parameter("cursor", cursor)
            parameter("limit", limit)
        }.body<VideoFeedResponse>()
        
        return VideoFeedData(
            items = response.items.map { VideoResponseMapper.mapToRight(it) },
            nextCursor = response.nextCursor
        )
    }

    override suspend fun toggleLike(videoId: Long): Boolean {
        return client.post("videos/$videoId/like").body<Map<String, Boolean>>()["liked"] ?: false
    }

    override suspend fun getComments(videoId: Long, cursor: Long?, limit: Int): CommentFeedData {
        val response = client.get("videos/$videoId/comments") {
            parameter("cursor", cursor)
            parameter("limit", limit)
        }.body<CommentFeedResponse>()

        return CommentFeedData(
            items = response.items.map { CommentResponseMapper.mapToRight(it) },
            nextCursor = response.nextCursor
        )
    }

    override suspend fun postComment(videoId: Long, content: String): CommentData {
        return client.post("videos/$videoId/comments") {
            setBody(buildJsonObject { put("content", content) })
        }.body<CommentResponse>().let { CommentResponseMapper.mapToRight(it) }
    }
}
