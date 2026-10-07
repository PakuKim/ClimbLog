package io.paku.climblog.presentation.video

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.paku.climblog.contract.video.ClaimJobRequest
import io.paku.climblog.contract.video.ClaimJobResponse
import io.paku.climblog.contract.video.CompleteJobRequest
import io.paku.climblog.contract.video.FailedJobRequest
import io.paku.climblog.contract.video.TranscodingJobResponse
import io.paku.climblog.domain.TranscodingJobRepository
import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.ext.now
import io.paku.climblog.domain.model.video.JobStatus
import io.paku.climblog.domain.model.video.TranscodingJob
import io.paku.climblog.domain.model.video.VideoStatus
import kotlinx.datetime.LocalDateTime
import org.koin.ktor.ext.inject

class WorkerAuthConfig {
    var authToken: String = ""
}

val WorkerAuthPlugin = createRouteScopedPlugin("WorkerAuthPlugin", ::WorkerAuthConfig) {
    val expectedToken = pluginConfig.authToken
    onCall { call ->
        val providedToken = call.request.headers["X-Worker-Token"]
        if (expectedToken.isBlank() || providedToken != expectedToken) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid or missing worker token"))
        }
    }
}

fun Route.workerRoutes(workerAuthToken: String) {
    val transcodingJobRepository: TranscodingJobRepository by inject()
    val videoRepository: VideoRepository by inject()

    route("/internal/v1/transcoding/jobs") {
        install(WorkerAuthPlugin) {
            authToken = workerAuthToken
        }

        post("/claim") {
            val request = call.receive<ClaimJobRequest>()
            val claimedJob = transcodingJobRepository.claimNextJob(
                workerId = request.workerId,
                leaseSeconds = request.leaseSeconds
            )

            call.respond(
                HttpStatusCode.OK,
                ClaimJobResponse(job = claimedJob?.toResponse())
            )
        }

        post("/{id}/complete") {
            val jobId = call.parameters["id"]?.toLongOrNull()
            if (jobId == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid job ID"))
                return@post
            }

            val request = call.receive<CompleteJobRequest>()
            val job = transcodingJobRepository.findById(jobId)
            if (job == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Job not found"))
                return@post
            }

            // Verify worker ownership and lease validity
            val now = LocalDateTime.now()
            if (job.status != JobStatus.PROCESSING || job.workerId != request.workerId || job.lockedUntil == null || job.lockedUntil <= now) {
                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf("error" to "Worker is not the valid owner of an active lease for this job")
                )
                return@post
            }

            // Complete job and update Video
            transcodingJobRepository.updateStatus(job.id, JobStatus.COMPLETED)
            videoRepository.updateUrls(job.videoId, request.hlsUrl, request.thumbnailUrl)
            videoRepository.updateStatus(job.videoId, VideoStatus.READY)

            call.respond(HttpStatusCode.OK, mapOf("status" to "COMPLETED"))
        }

        post("/{id}/failed") {
            val jobId = call.parameters["id"]?.toLongOrNull()
            if (jobId == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid job ID"))
                return@post
            }

            val request = call.receive<FailedJobRequest>()
            val job = transcodingJobRepository.findById(jobId)
            if (job == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Job not found"))
                return@post
            }

            // Verify worker ownership and lease validity
            val now = LocalDateTime.now()
            if (job.status != JobStatus.PROCESSING || job.workerId != request.workerId || job.lockedUntil == null || job.lockedUntil <= now) {
                call.respond(
                    HttpStatusCode.Forbidden,
                    mapOf("error" to "Worker is not the valid owner of an active lease for this job")
                )
                return@post
            }

            if (job.attempt < 3) {
                // Allow retry: reset status to QUEUED so another claim can pick it up
                transcodingJobRepository.updateStatus(job.id, JobStatus.QUEUED, errorMessage = request.errorMessage)
            } else {
                // Max retries exceeded: mark job and video as FAILED
                transcodingJobRepository.updateStatus(job.id, JobStatus.FAILED, errorMessage = request.errorMessage)
                videoRepository.updateStatus(job.videoId, VideoStatus.FAILED)
            }

            call.respond(HttpStatusCode.OK, mapOf("status" to "FAILED_RECORDED"))
        }
    }
}

private fun TranscodingJob.toResponse() = TranscodingJobResponse(
    id = id,
    videoId = videoId,
    inputKey = inputKey,
    outputPrefix = outputPrefix,
    status = status.name,
    attempt = attempt,
    workerId = workerId,
    lockedUntil = lockedUntil?.toString()
)
