package io.paku.climblog.domain.model.video

import io.paku.climblog.domain.ext.now
import kotlinx.datetime.LocalDateTime

data class TranscodingJob(
    val id: Long = 0L,
    val videoId: Long,
    val inputKey: String,
    val outputPrefix: String,
    val status: JobStatus = JobStatus.QUEUED,
    val attempt: Int = 0,
    val workerId: String? = null,
    val lockedUntil: LocalDateTime? = null,
    val errorMessage: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
