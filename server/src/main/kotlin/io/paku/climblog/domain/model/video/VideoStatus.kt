package io.paku.climblog.domain.model.video

import kotlinx.serialization.Serializable

@Serializable
enum class VideoStatus {
    UPLOADING,
    PROCESSING,
    READY,
    FAILED
}
