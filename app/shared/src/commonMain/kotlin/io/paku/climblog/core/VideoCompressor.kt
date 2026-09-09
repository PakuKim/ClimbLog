package io.paku.climblog.core

import io.paku.climblog.business.domain.model.video.VideoQuality

interface VideoCompressor {
    /**
     * Compresses the video to the specified quality.
     * Returns a [PlatformMedia] pointing to the compressed file.
     */
    suspend fun compress(
        source: PlatformMedia,
        quality: VideoQuality
    ): PlatformMedia
}
