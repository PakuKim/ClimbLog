package io.paku.climblog.domain.provider

import io.paku.climblog.domain.model.MediaSource
import io.paku.climblog.domain.model.video.VideoQuality

interface VideoCompressor {
    suspend fun compress(
        source: MediaSource,
        quality: VideoQuality
    ): MediaSource
}
