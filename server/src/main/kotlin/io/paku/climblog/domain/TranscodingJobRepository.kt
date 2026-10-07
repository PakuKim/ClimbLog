package io.paku.climblog.domain

import io.paku.climblog.domain.model.video.JobStatus
import io.paku.climblog.domain.model.video.TranscodingJob

interface TranscodingJobRepository {
    suspend fun save(job: TranscodingJob): TranscodingJob
    suspend fun claimNextJob(workerId: String, leaseSeconds: Long = 600): TranscodingJob?
    suspend fun updateStatus(id: Long, status: JobStatus, errorMessage: String? = null): Boolean
    suspend fun findById(id: Long): TranscodingJob?
    suspend fun findByVideoId(videoId: Long): TranscodingJob?
}
