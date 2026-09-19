package io.paku.climblog.business.domain.provider

import io.paku.climblog.business.domain.model.MediaSource
import io.paku.climblog.business.domain.model.video.VideoQuality

interface VideoCompressor {
    suspend fun compress(
        source: MediaSource,
        quality: VideoQuality
    ): MediaSource
}
