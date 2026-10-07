package io.paku.climblog

import io.paku.climblog.data.TranscodingJobRepositoryImpl
import io.paku.climblog.data.UserRepositoryImpl
import io.paku.climblog.data.VideoRepositoryImpl
import io.paku.climblog.data.database.DatabaseFactory
import io.paku.climblog.domain.interactor.video.RegisterVideoUseCase
import io.paku.climblog.domain.model.user.User
import io.paku.climblog.domain.model.video.JobStatus
import io.paku.climblog.domain.model.video.VideoStatus
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RegisterVideoUseCaseTest {

    private val videoRepository = VideoRepositoryImpl()
    private val transcodingJobRepository = TranscodingJobRepositoryImpl()
    private val userRepository = UserRepositoryImpl()

    private var testUserId = 0L

    @BeforeTest
    fun setup() {
        DatabaseFactory.init(
            driver = "org.h2.Driver",
            url = "jdbc:h2:mem:test_register_${System.currentTimeMillis()};DB_CLOSE_DELAY=-1"
        )

        runBlocking {
            val user = userRepository.save(
                User(
                    name = "Register Tester",
                    handle = "reg_tester_${System.currentTimeMillis()}",
                    age = 26,
                    height = 170,
                    armReach = 175,
                    gender = "F",
                    social = emptyMap()
                )
            )
            testUserId = user.id
        }
    }

    @Test
    fun testRegisterVideo_createsVideoAndTranscodingJobAtomically() = runBlocking {
        val useCase = RegisterVideoUseCase(videoRepository)

        val result = useCase(
            userId = testUserId,
            title = "Climbing Send",
            description = "Great boulder problem",
            s3Key = "raw/550e8400-e29b-41d4-a716-446655440000_climbing.mp4",
            cloudFrontDomain = "cdn.example.com"
        )

        assertTrue(result.isSuccess)
        val video = result.getOrThrow()

        // Verify Video initial state
        assertEquals(VideoStatus.PROCESSING, video.status)
        assertNull(video.hlsUrl)
        assertNull(video.thumbnailUrl)

        // Verify TranscodingJob was created atomically with exact inputKey and outputPrefix
        val job = transcodingJobRepository.findByVideoId(video.id)
        assertNotNull(job)
        assertEquals(JobStatus.QUEUED, job.status)
        assertEquals(0, job.attempt)
        assertNull(job.workerId)
        assertNull(job.lockedUntil)
        assertEquals("raw/550e8400-e29b-41d4-a716-446655440000_climbing.mp4", job.inputKey)
        assertEquals("processed/${video.id}/", job.outputPrefix)
    }

    @Test
    fun testRetryStateCleanup_resetsWorkerIdAndLockedUntilWhenQueued() = runBlocking {
        val useCase = RegisterVideoUseCase(videoRepository)
        val video = useCase(
            userId = testUserId,
            title = "Retry Test",
            description = null,
            s3Key = "raw/retry.mp4",
            cloudFrontDomain = "cdn.example.com"
        ).getOrThrow()

        val job = transcodingJobRepository.findByVideoId(video.id)
        assertNotNull(job)

        // Claim job
        val claimed = transcodingJobRepository.claimNextJob("worker-1", 600)
        assertNotNull(claimed)
        assertEquals("worker-1", claimed.workerId)
        assertEquals(JobStatus.PROCESSING, claimed.status)

        // Reset to QUEUED for retry
        transcodingJobRepository.updateStatus(job.id, JobStatus.QUEUED, errorMessage = "Failed attempt")

        val resetJob = transcodingJobRepository.findById(job.id)
        assertNotNull(resetJob)
        assertEquals(JobStatus.QUEUED, resetJob.status)
        assertNull(resetJob.workerId)
        assertNull(resetJob.lockedUntil)
        assertEquals("Failed attempt", resetJob.errorMessage)
    }
}
