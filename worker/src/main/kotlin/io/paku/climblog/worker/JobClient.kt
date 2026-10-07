package io.paku.climblog.worker

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.paku.climblog.contract.video.ClaimJobRequest
import io.paku.climblog.contract.video.ClaimJobResponse
import io.paku.climblog.contract.video.CompleteJobRequest
import io.paku.climblog.contract.video.FailedJobRequest
import io.paku.climblog.contract.video.TranscodingJobResponse
import kotlinx.serialization.json.Json

class JobClient(private val config: WorkerConfig) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    private val baseUrl = config.ktorBaseUrl.removeSuffix("/")

    suspend fun claimJob(): TranscodingJobResponse? {
        val response = client.post("$baseUrl/internal/v1/transcoding/jobs/claim") {
            header("X-Worker-Token", config.workerAuthToken)
            contentType(ContentType.Application.Json)
            setBody(ClaimJobRequest(workerId = config.workerId, leaseSeconds = config.leaseSeconds))
        }
        if (response.status == HttpStatusCode.OK) {
            val result = response.body<ClaimJobResponse>()
            return result.job
        }
        return null
    }

    suspend fun completeJob(jobId: Long, hlsUrl: String, thumbnailUrl: String) {
        client.post("$baseUrl/internal/v1/transcoding/jobs/$jobId/complete") {
            header("X-Worker-Token", config.workerAuthToken)
            contentType(ContentType.Application.Json)
            setBody(CompleteJobRequest(workerId = config.workerId, hlsUrl = hlsUrl, thumbnailUrl = thumbnailUrl))
        }
    }

    suspend fun failJob(jobId: Long, errorMessage: String) {
        client.post("$baseUrl/internal/v1/transcoding/jobs/$jobId/failed") {
            header("X-Worker-Token", config.workerAuthToken)
            contentType(ContentType.Application.Json)
            setBody(FailedJobRequest(workerId = config.workerId, errorMessage = errorMessage))
        }
    }
}
