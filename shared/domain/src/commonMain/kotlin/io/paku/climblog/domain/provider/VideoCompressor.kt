package io.paku.climblog.domain.provider

interface VideoCompressor {
    suspend fun compress(
        source: io.paku.climblog.domain.model.MediaSource,
        quality: io.paku.climblog.domain.model.video.VideoQuality
    ): io.paku.climblog.domain.model.MediaSource
}
