package io.paku.climblog.business.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.paku.climblog.business.data.source.remote.VideoRemoteDataSource
import io.paku.climblog.business.domain.model.Comment
import io.paku.climblog.business.domain.model.Video
import io.paku.climblog.business.remote.dto.response.video.CommentResponse
import io.paku.climblog.business.remote.dto.response.video.PresignedUrlResponse
import io.paku.climblog.business.remote.dto.response.video.VideoFeedResponse
import io.paku.climblog.business.remote.dto.response.video.VideoResponse
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal class VideoRemoteDataSourceImpl(
    private val client: HttpClient
) : VideoRemoteDataSource {

    override suspend fun getPresignedUrl(fileName: String, contentType: String): Pair<String, String> {
        val response = client.post("api/v1/videos/presigned-url") {
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
        client.put(url) {
            contentType(ContentType.Video.Any) // Adjust based on input if possible
            setBody(bytes)
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
        return client.post("api/v1/videos") {
            setBody(
                buildJsonObject {
                    put("title", title)
                    put("description", description)
                    put("s3Key", s3Key)
                    put("cruxStartTime", cruxStartTime)
                    put("cruxEndTime", cruxEndTime)
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
        // We might need a specific endpoint for user's videos, 
        // or just use feed with filter. For now assume an endpoint.
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
    cruxStartTime = cruxStartTime,
    cruxEndTime = cruxEndTime,
    createdAt = createdAt
)
