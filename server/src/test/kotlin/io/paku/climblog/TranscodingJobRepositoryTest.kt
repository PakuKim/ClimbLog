package io.paku.climblog

import io.paku.climblog.data.TranscodingJobRepositoryImpl
import io.paku.climblog.data.UserRepositoryImpl
import io.paku.climblog.data.VideoRepositoryImpl
import io.paku.climblog.data.database.DatabaseFactory
import io.paku.climblog.domain.ext.now
import io.paku.climblog.domain.model.user.User
import io.paku.climblog.domain.model.video.JobStatus
import io.paku.climblog.domain.model.video.TranscodingJob
import io.paku.climblog.domain.model.video.Video
import io.paku.climblog.domain.model.video.VideoStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

class TranscodingJobRepositoryTest {

    private val jobRepository = TranscodingJobRepositoryImpl()
    private val videoRepository = VideoRepositoryImpl()
    private val userRepository = UserRepositoryImpl()

    private var testVideoId = 0L

    @BeforeTest
    fun setup() {
        DatabaseFactory.init(
            driver = "org.h2.Driver",
            url = "jdbc:h2:mem:test_job_repo_${System.currentTimeMillis()};DB_CLOSE_DELAY=-1"
        )

        runBlocking {
            val user = userRepository.save(
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
            val video = videoRepository.save(
                Video(
                    userId = user.id,
                    title = "Test Video",
                    description = null,
                    hlsUrl = "",
                    thumbnailUrl = null,
                    status = VideoStatus.PROCESSING
                )
            )
            testVideoId = video.id
        }
    }

    @Test
    fun testSaveAndFindTranscodingJob() = runBlocking {
        val job = jobRepository.save(
            TranscodingJob(
                videoId = testVideoId,
                inputKey = "raw/test.mp4",
                outputPrefix = "processed/$testVideoId/"
            )
        )

        assertTrue(job.id > 0)
        assertEquals(JobStatus.QUEUED, job.status)
        assertEquals(0, job.attempt)
        assertNull(job.workerId)

        val found = jobRepository.findById(job.id)
        assertNotNull(found)
        assertEquals(job.id, found.id)
        assertEquals("raw/test.mp4", found.inputKey)

        val foundByVideo = jobRepository.findByVideoId(testVideoId)
        assertNotNull(foundByVideo)
        assertEquals(job.id, foundByVideo.id)
    }

    @Test
    fun testClaimNextJob_atomicClaimAndLeaseAssignment() = runBlocking {
        val savedJob = jobRepository.save(
            TranscodingJob(
                videoId = testVideoId,
                inputKey = "raw/test.mp4",
                outputPrefix = "processed/$testVideoId/"
            )
        )

        val claimed = jobRepository.claimNextJob(workerId = "worker-1", leaseSeconds = 600)
        assertNotNull(claimed)
        assertEquals(savedJob.id, claimed.id)
        assertEquals(JobStatus.PROCESSING, claimed.status)
        assertEquals("worker-1", claimed.workerId)
        assertEquals(1, claimed.attempt)
        assertNotNull(claimed.lockedUntil)

        // Second claim attempt should return null because no queued or expired job exists
        val secondClaim = jobRepository.claimNextJob(workerId = "worker-2", leaseSeconds = 600)
        assertNull(secondClaim)
    }

    @Test
    fun testClaimNextJob_reclaimsExpiredJob() = runBlocking {
        val pastTime = (Clock.System.now() - 3600.seconds).toLocalDateTime(TimeZone.currentSystemDefault())

        // Save a job that is already in PROCESSING with an expired lockedUntil timestamp
        val expiredJob = jobRepository.save(
            TranscodingJob(
                videoId = testVideoId,
                inputKey = "raw/expired.mp4",
                outputPrefix = "processed/$testVideoId/",
                status = JobStatus.PROCESSING,
                attempt = 1,
                workerId = "crashed-worker",
                lockedUntil = pastTime
            )
        )

        val reclaimed = jobRepository.claimNextJob(workerId = "new-worker", leaseSeconds = 600)
        assertNotNull(reclaimed)
        assertEquals(expiredJob.id, reclaimed.id)
        assertEquals(JobStatus.PROCESSING, reclaimed.status)
        assertEquals("new-worker", reclaimed.workerId)
        assertEquals(2, reclaimed.attempt) // Attempt incremented
        assertTrue(reclaimed.lockedUntil!! > LocalDateTime.now())
    }

    @Test
    fun testConcurrentClaims_guaranteesSingleWorkerClaim() = runBlocking {
        jobRepository.save(
            TranscodingJob(
                videoId = testVideoId,
                inputKey = "raw/concurrent.mp4",
                outputPrefix = "processed/$testVideoId/"
            )
        )

        // Launch 5 concurrent workers attempting to claim the single job
        val results = (1..5).map { workerIndex ->
            async(Dispatchers.IO) {
                jobRepository.claimNextJob(workerId = "worker-$workerIndex", leaseSeconds = 600)
            }
        }.awaitAll()

        val claimedJobs = results.filterNotNull()
        assertEquals(1, claimedJobs.size, "Exactly one worker must successfully claim the job")
    }

    @Test
    fun testUpdateStatus() = runBlocking {
        val job = jobRepository.save(
            TranscodingJob(
                videoId = testVideoId,
                inputKey = "raw/test.mp4",
                outputPrefix = "processed/$testVideoId/"
            )
        )

        val success = jobRepository.updateStatus(job.id, JobStatus.COMPLETED)
        assertTrue(success)

        val updated = jobRepository.findById(job.id)
        assertNotNull(updated)
        assertEquals(JobStatus.COMPLETED, updated.status)
    }
}
