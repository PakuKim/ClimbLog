package io.paku.climblog.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.paku.climblog.contract.comment.CommentFeedResponse
import io.paku.climblog.contract.comment.CommentResponse
import io.paku.climblog.contract.video.PresignedPostRequest
import io.paku.climblog.contract.video.PresignedPostResponse
import io.paku.climblog.contract.video.VideoFeedResponse
import io.paku.climblog.contract.video.VideoResponse
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

internal class VideoRemoteDataSourceImpl(
    private val client: HttpClient
) : io.paku.climblog.data.source.remote.VideoRemoteDataSource {
    private companion object {
        const val PRESIGNED_POST = "videos/uploads/presigned-post"
        const val REGISTER = "videos"
    }

    override suspend fun getPresignedPost(fileName: String, contentType: String): io.paku.climblog.data.model.video.PresignedPostData {
        val response = client.post(PRESIGNED_POST) {
            setBody(PresignedPostRequest(fileName, contentType))
        }.body<PresignedPostResponse>()
        
        return _root_ide_package_.io.paku.climblog.data.model.video.PresignedPostData(
            url = response.url,
            fields = response.fields,
            objectKey = response.objectKey
        )
    }

    override suspend fun uploadVideoToS3Post(
        url: String,
        fields: Map<String, String>,
        videoBytes: ByteArray,
        onProgress: (Float) -> Unit
    ) {
        val uploadClient = HttpClient {
            install(HttpTimeout) {
                requestTimeoutMillis = 600_000 // 10 mins
            }
        }
        
        uploadClient.post(url) {
            setBody(MultiPartFormDataContent(
                formData {
                    fields.forEach { (key, value) ->
                        append(key, value)
                    }
                    append("file", videoBytes, Headers.build {
                        append(HttpHeaders.ContentType, "video/mp4")
                        append(HttpHeaders.ContentDisposition, "filename=\"video.mp4\"")
                    })
                }
            ))
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
    ): io.paku.climblog.data.model.video.VideoData {
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
        }.body<VideoResponse>().let { io.paku.climblog.remote.mapper.video.VideoResponseMapper.mapToRight(it) }
    }

    override suspend fun getVideos(
        type: String?,
        userId: Long?,
        sortBy: String,
        orderBy: String,
        cursor: Long?,
        limit: Int
    ): io.paku.climblog.data.model.video.VideoFeedData {
        val response = client.get("videos") {
            parameter("type", type)
            parameter("userId", userId)
            parameter("sortBy", sortBy)
            parameter("orderBy", orderBy)
            parameter("cursor", cursor)
            parameter("limit", limit)
        }.body<VideoFeedResponse>()
        
        return _root_ide_package_.io.paku.climblog.data.model.video.VideoFeedData(
            items = response.items.map { io.paku.climblog.remote.mapper.video.VideoResponseMapper.mapToRight(it) },
            nextCursor = response.nextCursor
        )
    }

    override suspend fun toggleLike(videoId: Long): Boolean {
        return client.post("videos/$videoId/like").body<Map<String, Boolean>>()["liked"] ?: false
    }

    override suspend fun getComments(videoId: Long, cursor: Long?, limit: Int): io.paku.climblog.data.model.comment.CommentFeedData {
        val response = client.get("videos/$videoId/comments") {
            parameter("cursor", cursor)
            parameter("limit", limit)
        }.body<CommentFeedResponse>()

        return _root_ide_package_.io.paku.climblog.data.model.comment.CommentFeedData(
            items = response.items.map { io.paku.climblog.remote.mapper.comment.CommentResponseMapper.mapToRight(it) },
            nextCursor = response.nextCursor
        )
    }

    override suspend fun postComment(videoId: Long, content: String): io.paku.climblog.data.model.comment.CommentData {
        return client.post("videos/$videoId/comments") {
            setBody(buildJsonObject { put("content", content) })
        }.body<CommentResponse>().let { io.paku.climblog.remote.mapper.comment.CommentResponseMapper.mapToRight(it) }
    }
}
