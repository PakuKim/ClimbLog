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
import io.paku.climblog.domain.ext.getUserId
import io.paku.climblog.domain.interactor.video.GetVideoCommentsUseCase
import io.paku.climblog.domain.interactor.video.GetVideoListUseCase
import io.paku.climblog.domain.interactor.video.PostCommentUseCase
import io.paku.climblog.domain.interactor.video.RegisterVideoUseCase
import io.paku.climblog.domain.interactor.video.ToggleLikeUseCase
import io.paku.climblog.domain.interactor.video.UpdateVideoStatusUseCase
import io.paku.climblog.domain.model.video.Video
import io.paku.climblog.domain.model.video.VideoComment
import io.paku.climblog.domain.model.video.VideoCrux
import io.paku.climblog.domain.model.video.VideoStatus
import io.paku.climblog.domain.provider.S3Provider
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.videoRoutes(
    s3Bucket: String,
    cloudFrontDomain: String
) {
    val s3Provider: S3Provider by inject()
    val getVideoListUseCase: GetVideoListUseCase by inject()
    val getVideoCommentsUseCase: GetVideoCommentsUseCase by inject()
    val toggleLikeUseCase: ToggleLikeUseCase by inject()
    val postCommentUseCase: PostCommentUseCase by inject()
    val registerVideoUseCase: RegisterVideoUseCase by inject()
    val updateVideoStatusUseCase: UpdateVideoStatusUseCase by inject()

    authenticate("auth-jwt") {
        route("/api/v1/videos") {
            get {
                val type = call.request.queryParameters["type"]
                val sortBy = call.request.queryParameters["sortBy"] ?: "CREATED_AT"
                val orderBy = call.request.queryParameters["orderBy"] ?: "DESC"
                val cursor = call.request.queryParameters["cursor"]?.toLongOrNull()
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
                val userId = call.request.queryParameters["userId"]?.toLongOrNull()
                
                val currentUserId = call.getUserId()

                getVideoListUseCase(
                    type = type,
                    userId = userId,
                    currentUserId = currentUserId,
                    cursor = cursor,
                    limit = limit,
                    sortBy = sortBy,
                    orderBy = orderBy
                ).onSuccess { videos ->
                    val nextCursor = if (videos.size >= limit) {
                        val lastVideo = videos.last()
                        if (type == "HOME" && cursor != null && cursor < 0) {
                            // Already in Phase 2
                            -lastVideo.id
                        } else if (type == "HOME" && videos.any { it.userId == currentUserId }) { 
                            // This is a simplification. Ideally repository tells us the phase.
                            // For now, let's keep it simple or adjust repository to return metadata.
                            lastVideo.id
                        } else {
                            lastVideo.id
                        }
                    } else null

                    call.respond(
                        HttpStatusCode.OK,
                        VideoFeedResponse(
                            items = videos.map { it.toResponse() },
                            nextCursor = nextCursor
                        )
                    )
                }.onFailure {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            route("/uploads") {
                post("/presigned-post") {
                    val request = call.receive<PresignedPostRequest>()
                    val s3Key = "raw/${UUID.randomUUID()}_${request.fileName}"
                    
                    val postData = s3Provider.generatePresignedPost(
                        bucketName = s3Bucket,
                        key = s3Key,
                        contentType = request.contentType
                    )
                    
                    call.respond(HttpStatusCode.OK, PresignedPostResponse(
                        url = postData.url,
                        fields = postData.fields,
                        objectKey = s3Key
                    ))
                }
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
                val thumbnailUrl = "https://$cloudFrontDomain/processed/$fileNameWithoutExt/_thumb.0000000.jpg"

                val video = Video(
                    userId = userId,
                    title = request.title,
                    description = request.description,
                    hlsUrl = hlsUrl,
                    thumbnailUrl = thumbnailUrl,
                    status = VideoStatus.PROCESSING,
                    videoCruxes = request.cruxes.map {
                        VideoCrux(
                            startTime = it.startTime,
                            endTime = it.endTime
                        )
                    }
                )

                registerVideoUseCase(
                    video = video,
                    s3Bucket = s3Bucket,
                    s3Key = request.s3Key,
                    fileNameWithoutExt = fileNameWithoutExt
                ).onSuccess { savedVideo ->
                    call.respond(HttpStatusCode.Created, savedVideo.toResponse())
                }.onFailure {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }

            // MediaConvert Webhook Callback
            route("/callback/mediaconvert") {
                post {
                    val body = call.receive<Map<String, String>>()
                    val videoId = body["videoId"]?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val status = body["status"] // COMPLETE, ERROR
                    
                    val videoStatus = when (status) {
                        "COMPLETE" -> VideoStatus.READY
                        "ERROR" -> VideoStatus.FAILED
                        else -> VideoStatus.PROCESSING
                    }
                    
                    updateVideoStatusUseCase(videoId, videoStatus).onSuccess {
                        call.respond(HttpStatusCode.OK)
                    }.onFailure {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            }

            route("/{id}") {
                post("/like") {
                    val userId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val videoId = call.parameters["id"]?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                    
                    toggleLikeUseCase(userId, videoId).onSuccess { isLiked ->
                        call.respond(HttpStatusCode.OK, mapOf("liked" to isLiked))
                    }.onFailure {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }

                route("/comments") {
                    get {
                        val videoId = call.parameters["id"]?.toLongOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)
                        val cursor = call.request.queryParameters["cursor"]?.toLongOrNull()
                        val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20

                        getVideoCommentsUseCase(videoId, cursor, limit).onSuccess { comments ->
                            val nextCursor = if (comments.size >= limit) comments.last().id else null
                            call.respond(
                                HttpStatusCode.OK,
                                CommentFeedResponse(
                                    items = comments.map { it.toResponse() },
                                    nextCursor = nextCursor
                                )
                            )
                        }.onFailure {
                            call.respond(HttpStatusCode.InternalServerError)
                        }
                    }

                    post {
                        val userId = call.principal<JWTPrincipal>()?.payload?.subject?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                        val videoId = call.parameters["id"]?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.BadRequest)
                        val request = call.receive<CommentRequest>()
                        
                        postCommentUseCase(userId, videoId, request.content).onSuccess { savedComment ->
                            call.respond(HttpStatusCode.Created, savedComment.toResponse())
                        }.onFailure {
                            call.respond(HttpStatusCode.InternalServerError)
                        }
                    }
                }
            }
        }
    }
}

private fun VideoComment.toResponse() = CommentResponse(
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
    status = status,
    cruxes = videoCruxes.map {
        VideoResponse.Crux(
            id = it.id,
            cruxStartTime = it.startTime,
            cruxEndTime = it.endTime
        )
    },
    createdAt = createdAt
)
