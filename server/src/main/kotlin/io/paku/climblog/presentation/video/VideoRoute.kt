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
import io.paku.climblog.contract.comment.CommentFeedResponse
import io.paku.climblog.contract.comment.CommentRequest
import io.paku.climblog.contract.comment.CommentResponse
import io.paku.climblog.contract.video.CruxResponse
import io.paku.climblog.contract.video.PresignedPutRequest
import io.paku.climblog.contract.video.PresignedPutResponse
import io.paku.climblog.contract.video.RegisterVideoRequest
import io.paku.climblog.contract.video.VideoFeedResponse
import io.paku.climblog.contract.video.VideoResponse
import io.paku.climblog.domain.ext.getUserId
import io.paku.climblog.domain.interactor.video.GetVideoCommentsUseCase
import io.paku.climblog.domain.interactor.video.GetVideoListUseCase
import io.paku.climblog.domain.interactor.video.PostCommentUseCase
import io.paku.climblog.domain.interactor.video.RegisterVideoUseCase
import io.paku.climblog.domain.interactor.video.ToggleLikeUseCase
import io.paku.climblog.domain.model.video.Video
import io.paku.climblog.domain.model.video.VideoComment
import io.paku.climblog.domain.model.video.VideoCrux
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
                post("/presigned-put") {
                    val request = call.receive<PresignedPutRequest>()
                    val objectKey = "raw/${UUID.randomUUID()}_${request.fileName}"

                    val uploadUrl = s3Provider.generatePresignedPutUrl(
                        bucketName = s3Bucket,
                        key = objectKey,
                        contentType = request.contentType
                    )

                    call.respond(
                        HttpStatusCode.OK,
                        PresignedPutResponse(
                            uploadUrl = uploadUrl,
                            objectKey = objectKey
                        )
                    )
                }
            }

            post {
                val userId = call.getUserId()
                val request = call.receive<RegisterVideoRequest>()

                registerVideoUseCase(
                    userId = userId,
                    title = request.title,
                    description = request.description,
                    s3Key = request.s3Key,
                    cloudFrontDomain = cloudFrontDomain,
                    cruxes = request.cruxes.map {
                        VideoCrux(
                            startTime = it.startTime,
                            endTime = it.endTime
                        )
                    }
                ).onSuccess { savedVideo ->
                    call.respond(HttpStatusCode.Created, savedVideo.toResponse())
                }.onFailure {
                    call.respond(HttpStatusCode.InternalServerError)
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
    status = io.paku.climblog.contract.video.VideoStatus.valueOf(status.name),
    cruxes = videoCruxes.map {
        CruxResponse(
            id = it.id,
            cruxStartTime = it.startTime,
            cruxEndTime = it.endTime
        )
    },
    createdAt = createdAt
)
