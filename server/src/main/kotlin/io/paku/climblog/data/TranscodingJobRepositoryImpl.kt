package io.paku.climblog.data

import io.paku.climblog.data.database.DatabaseFactory.dbQuery
import io.paku.climblog.data.database.table.video.TranscodingJobTable
import io.paku.climblog.domain.TranscodingJobRepository
import io.paku.climblog.domain.ext.now
import io.paku.climblog.domain.model.video.JobStatus
import io.paku.climblog.domain.model.video.TranscodingJob
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

internal class TranscodingJobRepositoryImpl : TranscodingJobRepository {

    private fun ResultRow.toDomain(): TranscodingJob = TranscodingJob(
        id = this[TranscodingJobTable.id].value,
        videoId = this[TranscodingJobTable.videoId].value,
        inputKey = this[TranscodingJobTable.inputKey],
        outputPrefix = this[TranscodingJobTable.outputPrefix],
        status = this[TranscodingJobTable.status],
        attempt = this[TranscodingJobTable.attempt],
        workerId = this[TranscodingJobTable.workerId],
        lockedUntil = this[TranscodingJobTable.lockedUntil],
        errorMessage = this[TranscodingJobTable.errorMessage],
        createdAt = this[TranscodingJobTable.createdAt],
        updatedAt = this[TranscodingJobTable.updatedAt]
    )

    override suspend fun save(job: TranscodingJob): TranscodingJob = dbQuery {
        val id = TranscodingJobTable.insert {
            it[videoId] = job.videoId
            it[inputKey] = job.inputKey
            it[outputPrefix] = job.outputPrefix
            it[status] = job.status
            it[attempt] = job.attempt
            it[workerId] = job.workerId
            it[lockedUntil] = job.lockedUntil
            it[errorMessage] = job.errorMessage
        }[TranscodingJobTable.id].value

        findById(id)!!
    }

    override suspend fun claimNextJob(workerId: String, leaseSeconds: Long): TranscodingJob? = dbQuery {
        val nowInstant = Clock.System.now()
        val now = nowInstant.toLocalDateTime(TimeZone.currentSystemDefault())
        val lockedUntilTime = (nowInstant + leaseSeconds.seconds).toLocalDateTime(TimeZone.currentSystemDefault())

        val candidate = TranscodingJobTable.selectAll()
            .where {
                (TranscodingJobTable.status eq JobStatus.QUEUED) or
                ((TranscodingJobTable.status eq JobStatus.PROCESSING) and (TranscodingJobTable.lockedUntil less now))
            }
            .orderBy(TranscodingJobTable.createdAt to SortOrder.ASC)
            .limit(1)
            .forUpdate()
            .singleOrNull() ?: return@dbQuery null

        val jobId = candidate[TranscodingJobTable.id].value
        val currentAttempt = candidate[TranscodingJobTable.attempt]

        TranscodingJobTable.update({ TranscodingJobTable.id eq jobId }) {
            it[status] = JobStatus.PROCESSING
            it[TranscodingJobTable.workerId] = workerId
            it[lockedUntil] = lockedUntilTime
            it[attempt] = currentAttempt + 1
            it[updatedAt] = now
        }

        findById(jobId)
    }

    override suspend fun updateStatus(id: Long, status: JobStatus, errorMessage: String?): Boolean = dbQuery {
        val now = LocalDateTime.now()
        TranscodingJobTable.update({ TranscodingJobTable.id eq id }) {
            it[TranscodingJobTable.status] = status
            it[TranscodingJobTable.errorMessage] = errorMessage
            if (status == JobStatus.QUEUED) {
                it[TranscodingJobTable.workerId] = null
                it[TranscodingJobTable.lockedUntil] = null
            }
            it[updatedAt] = now
        } > 0
    }

    override suspend fun findById(id: Long): TranscodingJob? = dbQuery {
        TranscodingJobTable.selectAll()
            .where { TranscodingJobTable.id eq id }
            .singleOrNull()
            ?.toDomain()
    }

    override suspend fun findByVideoId(videoId: Long): TranscodingJob? = dbQuery {
        TranscodingJobTable.selectAll()
            .where { TranscodingJobTable.videoId eq videoId }
            .singleOrNull()
            ?.toDomain()
    }
}
