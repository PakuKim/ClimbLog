package io.paku.climblog.domain.model.video

import kotlinx.serialization.Serializable

@Serializable
enum class JobStatus {
    QUEUED,
    PROCESSING,
    COMPLETED,
    FAILED
}
