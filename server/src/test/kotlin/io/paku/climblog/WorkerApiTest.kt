package io.paku.climblog

import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import io.paku.climblog.contract.video.ClaimJobRequest
import io.paku.climblog.contract.video.ClaimJobResponse
import io.paku.climblog.contract.video.CompleteJobRequest
import io.paku.climblog.contract.video.FailedJobRequest
import io.paku.climblog.data.TranscodingJobRepositoryImpl
import io.paku.climblog.data.UserRepositoryImpl
import io.paku.climblog.data.VideoRepositoryImpl
import io.paku.climblog.data.database.DatabaseFactory
import io.paku.climblog.domain.model.user.User
import io.paku.climblog.domain.model.video.JobStatus
import io.paku.climblog.domain.model.video.TranscodingJob
import io.paku.climblog.domain.model.video.Video
import io.paku.climblog.domain.model.video.VideoStatus
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

class WorkerApiTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val testToken = "secret-test-token"

    private fun testConfig(dbName: String) = MapApplicationConfig(
        "db.driver" to "org.h2.Driver",
        "db.url" to "jdbc:h2:mem:$dbName;DB_CLOSE_DELAY=-1",
        "redis.host" to "localhost",
        "redis.port" to "6379",
        "jwt.secret" to "test-secret",
        "jwt.issuer" to "test-issuer",
        "jwt.audience" to "test-audience",
        "aws.accessKey" to "test-key",
        "aws.secretKey" to "test-secret",
        "aws.region" to "ap-northeast-2",
        "aws.s3Bucket" to "test-bucket",
        "aws.cloudFrontDomain" to "test.cloudfront.net",
        "worker.authToken" to testToken
    )

    private fun setupTestVideoAndJob(dbName: String): Pair<Video, TranscodingJob> {
        DatabaseFactory.init(
            driver = "org.h2.Driver",
            url = "jdbc:h2:mem:$dbName;DB_CLOSE_DELAY=-1"
        )
        return runBlocking {
            val userRepo = UserRepositoryImpl()
            val videoRepo = VideoRepositoryImpl()
            val jobRepo = TranscodingJobRepositoryImpl()

            val user = userRepo.save(
                User(
                    name = "Tester",
                    handle = "tester_${System.currentTimeMillis()}",
                    age = 25,
                    height = 175,
                    armReach = 180,
                    gender = "M",
                    social = emptyMap()
                )
            )
            val video = videoRepo.save(
                Video(
                    userId = user.id,
                    title = "Worker Test Video",
                    description = null,
                    hlsUrl = "",
                    thumbnailUrl = null,
                    status = VideoStatus.PROCESSING
                )
            )
            val job = jobRepo.save(
                TranscodingJob(
                    videoId = video.id,
                    inputKey = "raw/test.mp4",
                    outputPrefix = "processed/${video.id}/"
                )
            )
            Pair(video, job)
        }
    }

    @Test
    fun testWorkerAuth_missingToken_returnsUnauthorized() = testApplication {
        val dbName = "test_db_auth_missing"
        environment { config = testConfig(dbName) }
        application { module() }

        val response = client.post("/internal/v1/transcoding/jobs/claim") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(ClaimJobRequest.serializer(), ClaimJobRequest(workerId = "worker-1")))
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun testWorkerAuth_invalidToken_returnsUnauthorized() = testApplication {
        val dbName = "test_db_auth_invalid"
        environment { config = testConfig(dbName) }
        application { module() }

        val response = client.post("/internal/v1/transcoding/jobs/claim") {
            header("X-Worker-Token", "wrong-token")
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(ClaimJobRequest.serializer(), ClaimJobRequest(workerId = "worker-1")))
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun testSuccessfulJobClaim() = testApplication {
        val dbName = "test_db_claim_success"
        environment { config = testConfig(dbName) }
        application { module() }

        val (_, job) = setupTestVideoAndJob(dbName)

        val response = client.post("/internal/v1/transcoding/jobs/claim") {
            header("X-Worker-Token", testToken)
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(ClaimJobRequest.serializer(), ClaimJobRequest(workerId = "worker-1")))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val claimResponse = json.decodeFromString(ClaimJobResponse.serializer(), response.bodyAsText())
        val claimedJob = claimResponse.job
        assertNotNull(claimedJob)
        assertEquals(job.id, claimedJob.id)
        assertEquals("worker-1", claimedJob.workerId)
        assertEquals("PROCESSING", claimedJob.status)
        assertEquals(1, claimedJob.attempt)
    }

    @Test
    fun testCompletionByOwningWorker_succeedsAndUpdateVideo() = testApplication {
        val dbName = "test_db_complete_success"
        environment { config = testConfig(dbName) }
        application { module() }

        val (video, job) = setupTestVideoAndJob(dbName)
        val jobRepo = TranscodingJobRepositoryImpl()
        val videoRepo = VideoRepositoryImpl()

        // Claim job first so worker-1 is active lease owner
        val claimed = runBlocking { jobRepo.claimNextJob("worker-1", 600) }
        assertNotNull(claimed)

        val response = client.post("/internal/v1/transcoding/jobs/${job.id}/complete") {
            header("X-Worker-Token", testToken)
            contentType(ContentType.Application.Json)
            setBody(
                json.encodeToString(
                    CompleteJobRequest.serializer(),
                    CompleteJobRequest(
                        workerId = "worker-1",
                        hlsUrl = "https://cdn.example.com/processed/${video.id}/master.m3u8",
                        thumbnailUrl = "https://cdn.example.com/processed/${video.id}/thumbnail.jpg"
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val updatedJob = runBlocking { jobRepo.findById(job.id) }
        assertNotNull(updatedJob)
        assertEquals(JobStatus.COMPLETED, updatedJob.status)

        val updatedVideo = runBlocking { videoRepo.findById(video.id) }
        assertNotNull(updatedVideo)
        assertEquals(VideoStatus.READY, updatedVideo.status)
        assertEquals("https://cdn.example.com/processed/${video.id}/master.m3u8", updatedVideo.hlsUrl)
        assertEquals("https://cdn.example.com/processed/${video.id}/thumbnail.jpg", updatedVideo.thumbnailUrl)
    }

    @Test
    fun testCompletionByNonOwningWorker_rejectedForbidden() = testApplication {
        val dbName = "test_db_complete_non_owner"
        environment { config = testConfig(dbName) }
        application { module() }

        val (video, job) = setupTestVideoAndJob(dbName)
        val jobRepo = TranscodingJobRepositoryImpl()

        // Claimed by worker-1
        runBlocking { jobRepo.claimNextJob("worker-1", 600) }

        // worker-2 attempts to complete worker-1's job
        val response = client.post("/internal/v1/transcoding/jobs/${job.id}/complete") {
            header("X-Worker-Token", testToken)
            contentType(ContentType.Application.Json)
            setBody(
                json.encodeToString(
                    CompleteJobRequest.serializer(),
                    CompleteJobRequest(
                        workerId = "worker-2", // Different worker!
                        hlsUrl = "https://cdn.example.com/processed/${video.id}/master.m3u8",
                        thumbnailUrl = "https://cdn.example.com/processed/${video.id}/thumbnail.jpg"
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun testCompletionWhenLeaseExpired_rejectedForbidden() = testApplication {
        val dbName = "test_db_complete_expired"
        environment { config = testConfig(dbName) }
        application { module() }

        val (video, _) = setupTestVideoAndJob(dbName)
        val jobRepo = TranscodingJobRepositoryImpl()

        val pastTime = (Clock.System.now() - 3600.seconds).toLocalDateTime(TimeZone.currentSystemDefault())
        val expiredJob = runBlocking {
            jobRepo.save(
                TranscodingJob(
                    videoId = video.id,
                    inputKey = "raw/expired.mp4",
                    outputPrefix = "processed/${video.id}/",
                    status = JobStatus.PROCESSING,
                    attempt = 1,
                    workerId = "worker-1",
                    lockedUntil = pastTime
                )
            )
        }

        // worker-1 attempts to complete after lease has expired
        val response = client.post("/internal/v1/transcoding/jobs/${expiredJob.id}/complete") {
            header("X-Worker-Token", testToken)
            contentType(ContentType.Application.Json)
            setBody(
                json.encodeToString(
                    CompleteJobRequest.serializer(),
                    CompleteJobRequest(
                        workerId = "worker-1",
                        hlsUrl = "https://cdn.example.com/processed/${video.id}/master.m3u8",
                        thumbnailUrl = "https://cdn.example.com/processed/${video.id}/thumbnail.jpg"
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun testFailedJobHandling_retryAllowedWhenAttemptLessThan3() = testApplication {
        val dbName = "test_db_failed_retry"
        environment { config = testConfig(dbName) }
        application { module() }

        val (_, job) = setupTestVideoAndJob(dbName)
        val jobRepo = TranscodingJobRepositoryImpl()

        // Claimed by worker-1 (attempt = 1)
        runBlocking { jobRepo.claimNextJob("worker-1", 600) }

        val response = client.post("/internal/v1/transcoding/jobs/${job.id}/failed") {
            header("X-Worker-Token", testToken)
            contentType(ContentType.Application.Json)
            setBody(
                json.encodeToString(
                    FailedJobRequest.serializer(),
                    FailedJobRequest(workerId = "worker-1", errorMessage = "Temporary network error")
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)

        // Job status should be reset to QUEUED for retry since attempt = 1 < 3
        val updatedJob = runBlocking { jobRepo.findById(job.id) }
        assertNotNull(updatedJob)
        assertEquals(JobStatus.QUEUED, updatedJob.status)
        assertEquals("Temporary network error", updatedJob.errorMessage)
    }

    @Test
    fun testFailedJobHandling_maxRetriesExceeded() = testApplication {
        val dbName = "test_db_failed_max"
        environment { config = testConfig(dbName) }
        application { module() }

        val (video, _) = setupTestVideoAndJob(dbName)
        val jobRepo = TranscodingJobRepositoryImpl()
        val videoRepo = VideoRepositoryImpl()

        val futureTime = (Clock.System.now() + 3600.seconds).toLocalDateTime(TimeZone.currentSystemDefault())
        val maxAttemptJob = runBlocking {
            jobRepo.save(
                TranscodingJob(
                    videoId = video.id,
                    inputKey = "raw/max.mp4",
                    outputPrefix = "processed/${video.id}/",
                    status = JobStatus.PROCESSING,
                    attempt = 3, // Already attempt 3
                    workerId = "worker-1",
                    lockedUntil = futureTime
                )
            )
        }

        val response = client.post("/internal/v1/transcoding/jobs/${maxAttemptJob.id}/failed") {
            header("X-Worker-Token", testToken)
            contentType(ContentType.Application.Json)
            setBody(
                json.encodeToString(
                    FailedJobRequest.serializer(),
                    FailedJobRequest(workerId = "worker-1", errorMessage = "FFmpeg error")
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)

        // Job status and video status should be marked FAILED since attempt = 3 >= 3
        val updatedJob = runBlocking { jobRepo.findById(maxAttemptJob.id) }
        assertNotNull(updatedJob)
        assertEquals(JobStatus.FAILED, updatedJob.status)

        val updatedVideo = runBlocking { videoRepo.findById(video.id) }
        assertNotNull(updatedVideo)
        assertEquals(VideoStatus.FAILED, updatedVideo.status)
    }

    @Test
    fun testNonexistentJob_returnsNotFound() = testApplication {
        val dbName = "test_db_nonexistent"
        environment { config = testConfig(dbName) }
        application { module() }

        val response = client.post("/internal/v1/transcoding/jobs/99999/complete") {
            header("X-Worker-Token", testToken)
            contentType(ContentType.Application.Json)
            setBody(
                json.encodeToString(
                    CompleteJobRequest.serializer(),
                    CompleteJobRequest(
                        workerId = "worker-1",
                        hlsUrl = "https://cdn.example.com/processed/99999/master.m3u8",
                        thumbnailUrl = "https://cdn.example.com/processed/99999/thumbnail.jpg"
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
