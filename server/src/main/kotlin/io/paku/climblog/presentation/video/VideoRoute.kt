package io.paku.climblog.presentation.video

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.paku.climblog.domain.CommentRepository
import io.paku.climblog.domain.LikeRepository
import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.interactor.video.GetRandomVideosUseCase
import io.paku.climblog.domain.model.Comment
import io.paku.climblog.domain.model.Video
import io.paku.climblog.domain.provider.S3Provider
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.videoRoutes(
    s3Bucket: String,
    cloudFrontDomain: String
) {
    val s3Provider: S3Provider by inject()
    val videoRepository: VideoRepository by inject()
    val commentRepository: CommentRepository by inject()
    val likeRepository: LikeRepository by inject()
    val getRandomVideosUseCase: GetRandomVideosUseCase by inject()

    authenticate("auth-jwt") {
        route("/api/v1/videos") {
            get("/random") {
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 18
                getRandomVideosUseCase(limit).onSuccess { videos ->
                    call.respond(HttpStatusCode.OK, videos.map { it.toResponse() })
                }.onFailure {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            post("/presigned-url") {
                val request = call.receive<PresignedUrlRequest>()
                val s3Key = "raw/${UUID.randomUUID()}_${request.fileName}"
                
                val url = s3Provider.generatePresignedUploadUrl(
                    bucketName = s3Bucket,
                    key = s3Key,
                    contentType = request.contentType
                )
                
                call.respond(
                    HttpStatusCode.OK,
                    PresignedUrlResponse(
                        presignedUrl = url.toString(),
                        s3Key = s3Key
                    )
                )
            }

            get("/feed") {
                val cursor = call.request.queryParameters["cursor"]?.toLongOrNull()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                
                val videos = videoRepository.findAllPaged(cursor, limit)
                val nextCursor = if (videos.size == limit) videos.last().id else null
                
                call.respond(
                    HttpStatusCode.OK,
                    VideoFeedResponse(
                        items = videos.map { it.toResponse() },
                        nextCursor = nextCursor
                    )
                )
            }

            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.subject?.toLongOrNull()
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }

                val request = call.receive<RegisterVideoRequest>()
                
                val fileNameWithoutExt = request.s3Key.substringAfterLast("/").substringBeforeLast(".")
                val hlsUrl = "https://$cloudFrontDomain/processed/$fileNameWithoutExt/master.m3u8"
                val thumbnailUrl = "https://$cloudFrontDomain/processed/$fileNameWithoutExt/thumbnail.jpg"

                val video = Video(
                    userId = userId,
                    title = request.title,
                    description = request.description,
                    hlsUrl = hlsUrl,
                    thumbnailUrl = thumbnailUrl,
                    cruxStartTime = request.cruxStartTime,
                    cruxEndTime = request.cruxEndTime,
                    createdAt = System.currentTimeMillis()
                )

                val savedVideo = videoRepository.save(video)
                call.respond(HttpStatusCode.Created, savedVideo.toResponse())
            }

            route("/{id}") {
                post("/like") {
                    val userId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val videoId = call.parameters["id"]?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                    
                    val isLiked = likeRepository.toggleLike(userId, videoId)
                    call.respond(HttpStatusCode.OK, mapOf("liked" to isLiked))
                }

                route("/comments") {
                    get {
                        val videoId = call.parameters["id"]?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
                        val comments = commentRepository.findAllByVideoId(videoId)
                        call.respond(HttpStatusCode.OK, comments.map { it.toResponse() })
                    }

                    post {
                        val userId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                        val videoId = call.parameters["id"]?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                        val request = call.receive<CommentRequest>()
                        
                        val comment = Comment(
                            videoId = videoId,
                            userId = userId,
                            userName = "", // Implementation handles fetching user info via join
                            userProfilePhotoUrl = null,
                            content = request.content,
                            createdAt = System.currentTimeMillis()
                        )
                        
                        val savedComment = commentRepository.save(comment)
                        call.respond(HttpStatusCode.Created, savedComment.toResponse())
                    }
                }
            }
        }
    }
}

private fun Comment.toResponse() = CommentResponse(
    id = id,
    videoId = videoId,
    userId = userId,
    userName = userName,
    userProfilePhotoUrl = userProfilePhotoUrl,
    content = content,
    createdAt = createdAt
)

private fun Video.toResponse() = VideoResponse(
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
