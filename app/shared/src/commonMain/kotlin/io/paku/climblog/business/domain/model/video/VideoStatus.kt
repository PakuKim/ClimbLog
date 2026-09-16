package io.paku.climblog.business.domain.model.video

import kotlinx.serialization.Serializable

@Serializable
enum class VideoStatus {
    UPLOADING,
    PROCESSING,
    READY,
    FAILED
}
