package io.paku.climblog.core

import io.paku.climblog.business.domain.model.MediaSource
import io.paku.climblog.business.domain.model.video.VideoQuality
import io.paku.climblog.business.domain.provider.VideoCompressor

interface PlatformVideoCompressor : VideoCompressor {
    override suspend fun compress(
        source: MediaSource,
        quality: VideoQuality
    ): PlatformMedia
}
