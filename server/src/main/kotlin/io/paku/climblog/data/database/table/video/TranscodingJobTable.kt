package io.paku.climblog.data.database.table.video

import io.paku.climblog.domain.model.video.JobStatus
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

internal object TranscodingJobTable : LongIdTable("transcoding_jobs") {
    val videoId = reference("video_id", VideoTable, ReferenceOption.CASCADE)
    val inputKey = varchar("input_key", 512)
    val outputPrefix = varchar("output_prefix", 512)
    val status = enumerationByName("status", 20, JobStatus::class).default(JobStatus.QUEUED)
    val attempt = integer("attempt").default(0)
    val workerId = varchar("worker_id", 128).nullable()
    val lockedUntil = datetime("locked_until").nullable()
    val errorMessage = text("error_message").nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}
