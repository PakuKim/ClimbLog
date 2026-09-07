package io.paku.climblog.business.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.utils.io.ByteReadChannel
import io.paku.climblog.business.data.source.remote.VideoRemoteDataSource
import io.paku.climblog.business.domain.model.Comment
import io.paku.climblog.business.domain.model.Crux
import io.paku.climblog.business.domain.model.Video
import io.paku.climblog.business.domain.model.video.PresignedPostRequest
import io.paku.climblog.business.domain.model.video.PresignedPostResponse
import io.paku.climblog.business.remote.dto.response.video.CommentResponse
import io.paku.climblog.business.remote.dto.response.video.CruxResponse
import io.paku.climblog.business.remote.dto.response.video.PresignedUrlResponse
import io.paku.climblog.business.remote.dto.response.video.VideoFeedResponse
import io.paku.climblog.business.remote.dto.response.video.VideoResponse
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

internal class VideoRemoteDataSourceImpl(
    private val client: HttpClient
) : VideoRemoteDataSource {
    private companion object {
        const val GET_PRESIGNED_URL = "videos/presigned-url"
        const val PRESIGNED_POST = "videos/uploads/presigned-post"
        const val REGISTER = "videos"
    }

    override suspend fun getPresignedPost(fileName: String, contentType: String): PresignedPostResponse {
        return client.post(PRESIGNED_POST) {
            setBody(PresignedPostRequest(fileName, contentType))
        }.body()
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
                    // S3 POST fields MUST come before 'file'
                    fields.forEach { (key, value) ->
                        append(key, value)
                    }
                    // 'file' MUST be the last field
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
    ): Video {
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
        }.body<VideoResponse>().toDomain()
    }

    override suspend fun getFeed(cursor: Long?, limit: Int): List<Video> {
        return client.get("api/v1/videos/feed") {
            parameter("cursor", cursor)
            parameter("limit", limit)
        }.body<VideoFeedResponse>().items.map { it.toDomain() }
    }

    override suspend fun getRandomVideos(limit: Int): List<Video> {
        return client.get("api/v1/videos/random") {
            parameter("limit", limit)
        }.body<List<VideoResponse>>().map { it.toDomain() }
    }

    override suspend fun getUserVideos(userId: Long): List<Video> {
        return client.get("api/v1/users/$userId/videos")
            .body<List<VideoResponse>>().map { it.toDomain() }
    }

    override suspend fun toggleLike(videoId: Long): Boolean {
        return client.post("api/v1/videos/$videoId/like").body<Map<String, Boolean>>()["liked"] ?: false
    }

    override suspend fun getComments(videoId: Long): List<Comment> {
        return client.get("api/v1/videos/$videoId/comments").body<List<CommentResponse>>().map { it.toDomain() }
    }

    override suspend fun postComment(videoId: Long, content: String): Comment {
        return client.post("api/v1/videos/$videoId/comments") {
            setBody(buildJsonObject { put("content", content) })
        }.body<CommentResponse>().toDomain()
    }

    override suspend fun getPresignedUrl(fileName: String, contentType: String): Pair<String, String> {
        val response = client.post(GET_PRESIGNED_URL) {
            setBody(
                buildJsonObject {
                    put("fileName", fileName)
                    put("contentType", contentType)
                }
            )
        }.body<PresignedUrlResponse>()
        return response.presignedUrl to response.s3Key
    }

    override suspend fun uploadToS3(url: String, bytes: ByteArray, onProgress: (Float) -> Unit) {
        val a = ByteReadChannel(bytes)
        val uploadClient = HttpClient {
            install(HttpTimeout) {
                requestTimeoutMillis = 600_000
            }
        }
        uploadClient.put(url) {
            contentType(ContentType.Video.Any)
            setBody(a)
            onUpload { bytesSentTotal, contentLength ->
                if (contentLength != null && contentLength > 0) {
                    onProgress(bytesSentTotal.toFloat() / contentLength.toFloat())
                }
            }
        }
    }
}

private fun CommentResponse.toDomain() = Comment(
    id = id,
    videoId = videoId,
    userId = userId,
    userName = userName,
    userProfilePhotoUrl = userProfilePhotoUrl,
    content = content,
    createdAt = createdAt
)

private fun VideoResponse.toDomain() = Video(
    id = id,
    userId = userId,
    title = title,
    description = description,
    hlsUrl = hlsUrl,
    thumbnailUrl = thumbnailUrl,
    cruxes = cruxes.map { it.toDomain(id) },
    createdAt = createdAt
)

private fun CruxResponse.toDomain(videoId: Long) = Crux(
    id = id,
    videoId = videoId,
    startTime = cruxStartTime,
    endTime = cruxEndTime
)
